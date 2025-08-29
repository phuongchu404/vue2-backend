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
import vn.mk.eid.web.dto.request.IdentityRecordCreateRequest;
import vn.mk.eid.web.dto.request.IdentityRecordUpdateRequest;
import vn.mk.eid.web.dto.request.QueryIdentityRecordRequest;
import vn.mk.eid.web.dto.response.AnthropometryResponse;
import vn.mk.eid.web.dto.response.IdentityRecordResponse;
import vn.mk.eid.web.dto.response.PhotoResponse;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.IdentityRecordRepositoryCustom;
import vn.mk.eid.web.service.IdentityRecordService;
import vn.mk.eid.web.service.MinioService;
import vn.mk.eid.web.specification.IdentityRecordSpecification;
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
                .orElseThrow(() -> new IllegalArgumentException("Detainee not found with code: " + request.getDetaineeCode()));

        //mapper request to IdentityRecordEntity
        IdentityRecordEntity identityRecord = new IdentityRecordEntity();
        identityRecord.setDetaineeId(detainee.getId());
        identityRecord.setCreatedPlace(request.getCreatedPlace());
        identityRecord.setReasonNote(request.getReasonNote());
        identityRecord.setArrestDate(request.getArrestDate());
        identityRecord.setArrestUnit(request.getArrestUnit());
        identityRecord.setFpClassification(request.getFpClassification());
        identityRecord.setDp(request.getDp());
        identityRecord.setTw(request.getTw());
        identityRecord.setAkFileNo(request.getAkFileNo());
        identityRecord.setNotes(request.getNotes());

        identityRecord = identityRecordRepository.save(identityRecord);

        //mapper request to AnthropometryEntity
        AnthropometryEntity anthropometry = new AnthropometryEntity();
        anthropometry.setIdentityRecordId(identityRecord.getId());
        anthropometry.setFaceShape(request.getFaceShape());
        anthropometry.setHeightCm(request.getHeightCm());
        anthropometry.setNoseBridge(request.getNoseBridge());
        anthropometry.setDistinctiveMarks(request.getDistinctiveMarks());
        anthropometry.setEarLowerFold(request.getEarLowerFold());
        anthropometry.setEarLobe(request.getEarLobe());

        anthropometry = anthropometryRepository.save(anthropometry);

        Map<Integer, MultipartFile> fileMap = createFileMap(front, leftProfile, rightProfile);
        List<PhotoResponse> photoResponses = fileUploads(detainee.getDetentionCenterId(), identityRecord.getId(),
                detainee.getDetaineeCode(), detainee.getIdNumber(), fileMap, new HashMap<>());

        //response
        IdentityRecordResponse response = convertToResponse(identityRecord, detainee);
        response.setAnthropometry(convertAnthropometryToResponse(anthropometry));
        response.setPhoto(photoResponses);

        log.info("Created identity record for detainee: {}", detainee.getDetaineeCode());
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
        IdentityRecordEntity identityRecord = identityRecordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Identity record not found"));
        DetaineeEntity detainee = detaineeRepository.findById(identityRecord.getDetaineeId()).orElseThrow(() -> new ResourceNotFoundException("Detainee not found"));

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
        response.setPhoto(photoResponses);

        log.info("Updated identity record for detainee: {}", detainee.getDetaineeCode());
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
                .orElseThrow(() -> new ResourceNotFoundException("Identity record not found"));

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
        response.setPhoto(photoResponses);

        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult getIdentityRecordByDetaineeId(Long detaineeId) {
        DetaineeEntity detainee = detaineeRepository.findById(detaineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Detainee not found"));

        List<IdentityRecordEntity> records = identityRecordRepository.findByDetaineeId(detainee.getId());
        List<IdentityRecordResponse> recordResponses = records.stream().map(record ->{
            return convertToResponse(record, detainee);
        }).collect(Collectors.toList());

        return ServiceResult.ok(recordResponses);
    }

    @Override
    public ServiceResult getIdentityRecordWithPaging(QueryIdentityRecordRequest request, Pageable pageable) {
        Page<IdentityRecordResponse> page = identityRecordRepositoryCustom.getWithPaging(request, pageable);
        return ServiceResult.ok(Paging.<IdentityRecordResponse>builder().content(page.getContent()).totalElements(page.getTotalElements()).build());
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
        Pair<String, String> uploadData = minioService.uploadFile(file, fileName, dir);
//        String objectUrl = minioService.getFileUrl(objectKey);

//         Save photo record
        if (photo == null) {
            photo = new PhotoEntity();
        }
        photo.setIdentityRecordId(identityRecordId);
        photo.setView(viewCode);
        photo.setBucket(dir);
        photo.setObjectKey(uploadData.getRight());
        photo.setObjectUrl(uploadData.getLeft());
        photo.setMimeType(file.getContentType());
        photo.setSizeBytes(file.getSize());

        photo = photoRepository.save(photo);
        log.info("Uploaded photo for identity record: {}, view: {}", identityRecordId, viewCode);
//
        return convertPhotoToResponse(photo, null);
    }

    @Override
    public ServiceResult getPhotos(Long identityRecordId) {
        IdentityRecordEntity identityRecord = identityRecordRepository.findById(identityRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity record not found"));

        List<PhotoEntity> photos = photoRepository.findByIdentityRecordId(identityRecord.getId());
        return ServiceResult.ok(photos.stream().map(item -> convertPhotoToResponse(item, null)).collect(Collectors.toList()));
    }

    private IdentityRecordResponse convertToResponse(IdentityRecordEntity record, DetaineeEntity detainee) {
        IdentityRecordResponse response = new IdentityRecordResponse();
        response.setId(record.getId());
        response.setDetaineeId(record.getDetaineeId());
        response.setDetaineeName(detainee.getFullName());
        response.setDetaineeCode(detainee.getDetaineeCode());
        response.setCreatedPlace(record.getCreatedPlace());
        response.setReasonNote(record.getReasonNote());
        response.setArrestDate(record.getArrestDate());
        response.setArrestUnit(record.getArrestUnit());
        response.setFpClassification(record.getFpClassification());
        response.setDp(record.getDp());
        response.setTw(record.getTw());
        response.setAkFileNo(record.getAkFileNo());
        response.setNotes(record.getNotes());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());


        return response;
    }

    private PhotoResponse convertPhotoToResponse(PhotoEntity photo, @Nullable Boolean isGenLink) {
        PhotoResponse response = new PhotoResponse();
        response.setId(photo.getId());
        response.setIdentityRecordId(photo.getIdentityRecordId());
        response.setView(photo.getView());
        response.setBucket(photo.getBucket());
        response.setObjectUrl(photo.getObjectUrl());
        response.setMimeType(photo.getMimeType());
        response.setSizeBytes(photo.getSizeBytes());
        response.setCreatedAt(photo.getCreatedAt());

        if (Objects.equals(isGenLink, Boolean.TRUE)) {
            response.setLinkUrl(minioService.getFileUrl(photo.getObjectUrl(), photo.getBucket(), MinioService.DownloadOption.builder().isPublic(false).build()));
        }
        return response;
    }

    private AnthropometryResponse convertAnthropometryToResponse(AnthropometryEntity anthropometry) {
        AnthropometryResponse response = new AnthropometryResponse();
        response.setIdentityRecordId(anthropometry.getIdentityRecordId());
        response.setFaceShape(anthropometry.getFaceShape());
        response.setHeightCm(anthropometry.getHeightCm());
        response.setNoseBridge(anthropometry.getNoseBridge());
        response.setDistinctiveMarks(anthropometry.getDistinctiveMarks());
        response.setEarLowerFold(anthropometry.getEarLowerFold());
        response.setEarLobe(anthropometry.getEarLobe());
        response.setCreatedAt(anthropometry.getCreatedAt());
        response.setUpdatedAt(anthropometry.getUpdatedAt());
        return response;
    }
}
