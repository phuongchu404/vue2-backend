package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.AnthropometryEntity;
import vn.mk.eid.common.dao.entity.DetaineeEntity;
import vn.mk.eid.common.dao.entity.IdentityRecordEntity;
import vn.mk.eid.common.dao.entity.PhotoEntity;
import vn.mk.eid.common.dao.repository.AnthropometryRepository;
import vn.mk.eid.common.dao.repository.DetaineeRepository;
import vn.mk.eid.common.dao.repository.IdentityRecordRepository;
import vn.mk.eid.common.dao.repository.PhotoRepository;
import vn.mk.eid.common.data.Paging;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.PhotoView;
import vn.mk.eid.web.constant.WebConstants;
import vn.mk.eid.web.dto.request.identity_record.IdentityRecordCreateRequest;
import vn.mk.eid.web.dto.request.identity_record.IdentityRecordUpdateRequest;
import vn.mk.eid.web.dto.request.identity_record.QueryIdentityRecordRequest;
import vn.mk.eid.web.dto.response.AnthropometryResponse;
import vn.mk.eid.web.dto.response.IdentityRecordResponse;
import vn.mk.eid.web.dto.response.PhotoResponse;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.IdentityRecordRepositoryCustom;
import vn.mk.eid.web.service.IdentityRecordService;
import vn.mk.eid.web.service.MinioService;
import vn.mk.eid.web.utils.FileUtil;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IdentityRecordServiceImpl implements IdentityRecordService {

    private final IdentityRecordRepository identityRecordRepository;
    private final IdentityRecordRepositoryCustom identityRecordRepositoryCustom;
    private final DetaineeRepository detaineeRepository;
    private final PhotoRepository photoRepository;
    private final AnthropometryRepository anthropometryRepository;
    private final MinioService minioService;

    @Override
    public ServiceResult createIdentityRecord(
            IdentityRecordCreateRequest request,
            MultipartFile front, MultipartFile leftProfile, MultipartFile rightProfile
    ) {
        DetaineeEntity detainee = detaineeRepository.findByDetaineeCode(request.getDetaineeCode())
                .orElseThrow(() -> new BadRequestException(String.format(ExceptionConstants.DETAINEE_NOT_FOUND_WITH_CODE, request.getDetaineeCode())));

        if (!identityRecordRepository.findByDetaineeId(detainee.getId()).isEmpty()) {
            throw new BadRequestException(ExceptionConstants.DUPLICATE_IDENTITY_RECORD);
        }

        IdentityRecordEntity identityRecord = new IdentityRecordEntity();
        identityRecord.setDetaineeId(detainee.getId());
        BeanUtils.copyProperties(request, identityRecord);
        identityRecord = identityRecordRepository.save(identityRecord);

        AnthropometryEntity anthropometry = new AnthropometryEntity();
        anthropometry.setIdentityRecordId(identityRecord.getId());
        BeanUtils.copyProperties(request, anthropometry);
        anthropometry = anthropometryRepository.save(anthropometry);

        Map<Integer, MultipartFile> fileMap = createFileMap(front, leftProfile, rightProfile);
        List<PhotoResponse> photoResponses = fileUploads(detainee.getDetentionCenterId(), identityRecord.getId(),
                detainee.getDetaineeCode(), detainee.getIdNumber(), fileMap, new HashMap<>());

        // response
        IdentityRecordResponse response = convertToResponse(identityRecord, detainee);
        response.setAnthropometry(convertAnthropometryToResponse(anthropometry));
        response.setPhotos(photoResponses);

        log.info("[IDENTITY_RECORD] Created for detainee: {}", detainee.getDetaineeCode());
        return ServiceResult.ok(response);
    }

    @NotNull
    private static Map<Integer, MultipartFile> createFileMap(MultipartFile front, MultipartFile leftProfile, MultipartFile rightProfile) {
        Map<Integer, MultipartFile> fileMap = new HashMap<>();
        putDataFileMap(front, PhotoView.FRONT, fileMap);
        putDataFileMap(leftProfile, PhotoView.LEFT_PROFILE, fileMap);
        putDataFileMap(rightProfile, PhotoView.RIGHT_PROFILE, fileMap);
        return fileMap;
    }

    private static void putDataFileMap(MultipartFile file, PhotoView photoView, Map<Integer, MultipartFile> fileMap) {
        if (file != null && file.getSize() > 0) {
            fileMap.put(photoView.getId(), file);
        }
    }

    @Override
    public ServiceResult updateIdentityRecord(Long id, IdentityRecordUpdateRequest request, MultipartFile front, MultipartFile leftProfile, MultipartFile rightProfile) {
        IdentityRecordEntity identityRecord = identityRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.IDENTITY_RECORD_NOT_FOUND));
        DetaineeEntity detainee = detaineeRepository.findById(identityRecord.getDetaineeId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));

        BeanUtils.copyProperties(request, identityRecord);
        identityRecordRepository.save(identityRecord);
        IdentityRecordResponse response = convertToResponse(identityRecord, detainee);

        Optional<AnthropometryEntity> optionalAnthropometry = anthropometryRepository.findByIdentityRecordId(identityRecord.getId());
        if (optionalAnthropometry.isPresent()) {
            AnthropometryEntity anthropometryEntity = optionalAnthropometry.get();
            BeanUtils.copyProperties(request, anthropometryEntity);
            anthropometryRepository.save(anthropometryEntity);
            response.setAnthropometry(convertAnthropometryToResponse(anthropometryEntity));
        }

        Map<Integer, MultipartFile> fileMap = createFileMap(front, leftProfile, rightProfile);
        Map<String, PhotoEntity> photoMap = createPhotoMap(identityRecord);

        List<PhotoResponse> photoResponses = fileUploads(detainee.getDetentionCenterId(), identityRecord.getId(),
                detainee.getDetaineeCode(), detainee.getIdNumber(), fileMap, photoMap);
        response.setPhotos(photoResponses);

        log.info("[IDENTITY_RECORD] Updated for detainee: {}", detainee.getDetaineeCode());
        return ServiceResult.ok(response);
    }

    @NotNull
    private Map<String, PhotoEntity> createPhotoMap(IdentityRecordEntity identityRecord) {
        List<PhotoEntity> photos = photoRepository.findByIdentityRecordId(identityRecord.getId());
        Map<String, PhotoEntity> photoMap = new HashMap<>();
        for (PhotoEntity photo : photos) {
            String key = String.format("%s_%s", photo.getIdentityRecordId(), photo.getView());
            photoMap.put(key, photo);
        }
        return photoMap;
    }

    @Override
    public ServiceResult getIdentityRecord(Long id) {
        IdentityRecordEntity identityRecord = identityRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.IDENTITY_RECORD_NOT_FOUND));

        DetaineeEntity detainee = detaineeRepository.findById(identityRecord.getDetaineeId())
                .orElseThrow(() -> new IllegalArgumentException("Detainee not found with id: " + identityRecord.getDetaineeId()));

        IdentityRecordResponse response = convertToResponse(identityRecord, detainee);
        Optional<AnthropometryEntity> optionalAnthropometry = anthropometryRepository.findByIdentityRecordId(identityRecord.getId());
        optionalAnthropometry.ifPresent(anthropometryEntity -> response.setAnthropometry(convertAnthropometryToResponse(anthropometryEntity)));

        List<PhotoEntity> photos = photoRepository.findByIdentityRecordId(identityRecord.getId());
        List<PhotoResponse> photoResponses = new ArrayList<>();
        for (PhotoEntity photo : photos) {
            photoResponses.add(convertPhotoToResponse(photo, Boolean.TRUE));
        }
        response.setPhotos(photoResponses);

        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult getIdentityRecordByDetaineeId(Long detaineeId) {
        DetaineeEntity detainee = detaineeRepository.findById(detaineeId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));

        List<IdentityRecordEntity> records = identityRecordRepository.findByDetaineeId(detainee.getId());
        List<IdentityRecordResponse> recordResponses = records
                .stream()
                .map(record -> convertToResponse(record, detainee))
                .collect(Collectors.toList());

        return ServiceResult.ok(recordResponses);
    }

    @Override
    public ServiceResult getIdentityRecordWithPaging(QueryIdentityRecordRequest request, Pageable pageable) {
        Page<IdentityRecordResponse> page = identityRecordRepositoryCustom.getWithPaging(request, pageable);
        List<IdentityRecordResponse> list = page.getContent();
        if(!list.isEmpty()){
            for(IdentityRecordResponse response : list){
                Optional<AnthropometryEntity> optionalAnthropometry = anthropometryRepository.findByIdentityRecordId(response.getId());
                optionalAnthropometry.ifPresent(anthropometryEntity -> response.setAnthropometry(convertAnthropometryToResponse(anthropometryEntity)));

                List<PhotoEntity> photos = photoRepository.findByIdentityRecordId(response.getId());
                List<PhotoResponse> photoResponses = new ArrayList<>();
                for (PhotoEntity photo : photos) {
                    photoResponses.add(convertPhotoToResponse(photo, Boolean.TRUE));
                }
                response.setPhotos(photoResponses);
            }
        }
        return ServiceResult.ok(page);
    }

    private List<PhotoResponse> fileUploads(Integer detentionCenterId, Long identityRecordId, String detaineeCode, String idNumber, Map<Integer, MultipartFile> fileMap, Map<String, PhotoEntity> photoMap) {
        List<PhotoResponse> photoResponses = new ArrayList<>();

        for (Map.Entry<Integer, MultipartFile> entry : fileMap.entrySet()) {
            Integer view = entry.getKey();
            MultipartFile file = entry.getValue();

            if (file != null && !file.isEmpty()) {
                String viewCode = PhotoView.getValueById(view);
                String key = String.format("%s_%s", identityRecordId, viewCode);
                PhotoResponse photoResponse = uploadPhoto(detentionCenterId,identityRecordId, detaineeCode,idNumber, viewCode, file, photoMap.get(key));
                photoResponses.add(photoResponse);
            }
        }
        return photoResponses;
    }

    public PhotoResponse uploadPhoto(Integer detentionCenterId, Long identityRecordId,String detaineeCode,String idNumber, String viewCode, MultipartFile file, PhotoEntity photo) {
        // Check if photo with this view already exists
//        photoRepository.findByIdentityRecordIdAndView(identityRecordId, viewCode)
//                .ifPresent(existingPhoto -> {
//                    // Delete old photo from MinIO
//                    minioService.removeFile(existingPhoto.getBucket(), existingPhoto.getObjectUrl(), MinioService.DownloadOption.builder().isPublic(false).build());
//                    photoRepository.delete(existingPhoto);
//                });

//         Upload new photo to MinIO
        String extensionFile = FileUtil.getExtensionOfFile(file.getOriginalFilename());
        String fileName = WebConstants.IdentityRecord.IDENTITY_RECORD_PREFIX + identityRecordId + WebConstants.CommonSymbol.SHIFT_DASH +
                detaineeCode + WebConstants.CommonSymbol.SHIFT_DASH + viewCode;
        log.info("Uploading photo for identity record: {}, view: {}, fileName: {}", identityRecordId, viewCode, fileName);
        String dir = detentionCenterId + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.DETAINEE + WebConstants.CommonSymbol.FORWARD_SLASH +
                detaineeCode + WebConstants.CommonSymbol.DASH + idNumber  + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.IDENTITY;
        log.info("Uploading photo to directory: {}", dir);
        String uploadData = minioService.uploadFile(file, fileName, dir);
//        String objectUrl = minioService.getFileUrl(objectKey);

//         Save photo record
        if (photo == null) {
            photo = new PhotoEntity();
        }
        photo.setIdentityRecordId(identityRecordId);
        photo.setView(viewCode);
        photo.setBucket(dir);
//        photo.setObjectKey(uploadData.getRight());
        photo.setObjectUrl(uploadData);
        photo.setMimeType(file.getContentType());
        photo.setSizeBytes(file.getSize());

        photo = photoRepository.save(photo);
        log.info("Uploaded photo for identity record: {}, view: {}", identityRecordId, viewCode);
        return convertPhotoToResponse(photo, null);
    }

    @Override
    public ServiceResult deleteIdentityRecord(Long id) {
        IdentityRecordEntity identityRecord = identityRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.IDENTITY_RECORD_NOT_FOUND));

        Long identityRecordId = identityRecord.getId();
        anthropometryRepository.deleteByIdentityRecordId(identityRecordId);
        List<PhotoEntity> photos = photoRepository.findByIdentityRecordId(identityRecordId);
        for (PhotoEntity photo : photos) {
            minioService.removeFile(photo.getObjectUrl(),photo.getBucket(), MinioService.DownloadOption.builder().isPublic(false).build());
            photoRepository.delete(photo);
        }
        identityRecordRepository.delete(identityRecord);

        log.info("[IDENTITY_RECORD] deleted {}", identityRecord.getId());
        return ServiceResult.ok();
    }

    private IdentityRecordResponse convertToResponse(IdentityRecordEntity record, DetaineeEntity detainee) {
        IdentityRecordResponse response = new IdentityRecordResponse();
        response.setDetaineeName(detainee.getFullName());
        response.setDetaineeCode(detainee.getDetaineeCode());
        BeanUtils.copyProperties(record, response);

        return response;
    }

    private PhotoResponse convertPhotoToResponse(PhotoEntity photo, @Nullable Boolean isGenLink) {
        PhotoResponse response = new PhotoResponse();
        BeanUtils.copyProperties(photo, response);

        if (Objects.equals(isGenLink, Boolean.TRUE)) {
            response.setLinkUrl(minioService.getFileUrl(photo.getObjectUrl(), photo.getBucket(), MinioService.DownloadOption.builder().isPublic(false).build()));
        }
        return response;
    }

    private AnthropometryResponse convertAnthropometryToResponse(AnthropometryEntity anthropometry) {
        AnthropometryResponse response = new AnthropometryResponse();
        BeanUtils.copyProperties(anthropometry, response);
        return response;
    }
}
