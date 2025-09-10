package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.DetaineeEntity;
import vn.mk.eid.common.dao.entity.FingerprintCardEntity;
import vn.mk.eid.common.dao.entity.FingerprintImpressionEntity;
import vn.mk.eid.common.dao.repository.DetaineeRepository;
import vn.mk.eid.common.dao.repository.FingerprintCardRepository;
import vn.mk.eid.common.dao.repository.FingerprintImpressionRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.FingerKind;
import vn.mk.eid.web.constant.WebConstants;
import vn.mk.eid.web.dto.request.FingerprintCardCreateRequest;
import vn.mk.eid.web.dto.request.QueryFingerPrintRequest;
import vn.mk.eid.web.dto.response.FingerprintCardResponse;
import vn.mk.eid.web.dto.response.FingerprintImpressionResponse;
import vn.mk.eid.web.dto.response.PhotoResponse;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.FingerPrintRepositoryCustom;
import vn.mk.eid.web.service.FingerprintCardService;
import vn.mk.eid.web.service.MinioService;
import vn.mk.eid.web.utils.FileUtil;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FingerprintCardServiceImpl implements FingerprintCardService {
    private final FingerprintCardRepository fingerprintCardRepository;
    private final FingerprintImpressionRepository fingerprintImpressionRepository;
    private final DetaineeRepository detaineeRepository;
    private final MinioService minioService;
    private final FingerPrintRepositoryCustom fingerPrintRepositoryCustom;

    @Override
    public ServiceResult getWithPaging(Pageable pageable, QueryFingerPrintRequest request) {
        Page<FingerprintCardResponse> page = fingerPrintRepositoryCustom.getWithPaging(pageable, request);
        return ServiceResult.ok(page);
    }

    @Override
    public ServiceResult createFingerprintCard(FingerprintCardCreateRequest request) {
        DetaineeEntity detainee = detaineeRepository.findByDetaineeCode(request.getDetaineeCode())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));

        Optional<FingerprintCardEntity> optionalFingerCard = fingerprintCardRepository.findByDetaineeId(detainee.getId());
        if (optionalFingerCard.isPresent()) {
            throw new BadRequestException(ExceptionConstants.DUPLICATE_FINGER_PRINT_IMPRESSION);
        }

        FingerprintCardEntity card = new FingerprintCardEntity();
        card.setPersonId(detainee.getId());
        card.setCreatedDate(LocalDate.now());
        card.setCreatedPlace(request.getCreatedPlace());
        card.setDp(request.getDp());
        card.setTw(request.getTw());
        card.setFpFormula(request.getFpFormula());
        card.setReasonNote(request.getReasonNote());

        card = fingerprintCardRepository.save(card);
        Long fingerprintCardId = card.getId();

        uploadFingerPrint(request, fingerprintCardId, detainee, new HashMap<>());
        log.info("Created fingerprint card for person: {}", detainee.getDetaineeCode());
        return ServiceResult.ok(convertToResponse(card, detainee, false));
    }

    private void uploadFingerPrint(FingerprintCardCreateRequest request, Long fingerprintCardId, DetaineeEntity detainee, Map<String, FingerprintImpressionEntity> photoMap) {
        List<FingerprintImpressionEntity> impressions = new ArrayList<>();

        if (request.getFingerprintImages() != null && !request.getFingerprintImages().isEmpty()) {
            Map<String, MultipartFile> images = request.getFingerprintImages();
            for (Map.Entry<String, MultipartFile> entry : images.entrySet()) {
                String imageKey = entry.getKey();
                MultipartFile imageFile = entry.getValue();

                if (imageFile != null && !imageFile.isEmpty()) {
                    // upload image to MinIO
                    String extensionFile = FileUtil.getExtensionOfFile(imageFile.getOriginalFilename());
                    String fileName = WebConstants.Fingerprint.FINGERPRINT_PREFIX + fingerprintCardId + WebConstants.CommonSymbol.SHIFT_DASH +
                            detainee.getDetaineeCode() + WebConstants.CommonSymbol.SHIFT_DASH + imageKey;
                    log.info("Uploading fingerprint for identity record: {}, view: {}, fileName: {}", fingerprintCardId, imageKey, fileName);
                    String dir = detainee.getDetentionCenterId() + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.DETAINEE + WebConstants.CommonSymbol.FORWARD_SLASH +
                            detainee.getDetaineeCode() + WebConstants.CommonSymbol.DASH + detainee.getIdNumber() + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.IDENTITY;
                    log.info("Uploading fingerprint to directory: {}", dir);
                    String uploadData = minioService.uploadFile(imageFile, fileName, dir);

                    FingerprintImpressionEntity impression = photoMap.get(imageKey);
                    if (impression == null) {
                        impression = new FingerprintImpressionEntity();
                    }
                    impression.setFingerprintCardId(fingerprintCardId);
                    impression.setFinger(imageKey);
                    impression.setKind(getKindByFingerType(imageKey));
                    impression.setBucket(dir);
                    impression.setObjectUrl(uploadData);
                    impression.setImageKey(fileName);

                    impressions.add(impression);
                }
            }
            fingerprintImpressionRepository.saveAll(impressions);
        }
    }

    @Override
    public ServiceResult updateFingerprintCard(FingerprintCardCreateRequest request, Long id) {
        FingerprintCardEntity card = fingerprintCardRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionConstants.FINGER_PRINT_NOT_FOUND));
        DetaineeEntity detainee = detaineeRepository.findByDetaineeCode(request.getDetaineeCode())
                .orElseThrow(() -> new BadRequestException(ExceptionConstants.DETAINEE_NOT_FOUND));
        fingerprintCardRepository.findByPersonIdAndIdIsNot(detainee.getId(), id)
                .ifPresent(item -> { throw new BadRequestException(ExceptionConstants.DUPLICATE_FINGER_PRINT_IMPRESSION); });

        BeanUtils.copyProperties(request, card);

        List<FingerprintImpressionEntity> impressions = fingerprintImpressionRepository.findByFingerprintCardId(card.getId());
        Map<String, FingerprintImpressionEntity> photoMap = new HashMap<>();
        for (FingerprintImpressionEntity impression : impressions) {
            photoMap.put(impression.getFinger(), impression);
        }

        uploadFingerPrint(request, card.getId(), detainee, photoMap);
        log.info("Updated fingerprint card for person: {}", detainee.getDetaineeCode());
        return ServiceResult.ok(convertToResponse(card, detainee, false));
    }

    @Override
    public ServiceResult getFingerprintCardByDetaineeId(Long detaineeId) {
        DetaineeEntity person = detaineeRepository.findById(detaineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        FingerprintCardEntity card = fingerprintCardRepository.findByDetaineeId(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        return ServiceResult.ok(convertToResponse(card, person, true));
    }

    @Override
    public ServiceResult getFingerprintCardById(Long id) {
        FingerprintCardEntity card = fingerprintCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fingerprint card not found"));

        DetaineeEntity person = detaineeRepository.findById(card.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        return ServiceResult.ok(convertToResponse(card, person, true));
    }

    @Override
    public ServiceResult uploadFingerprintImpression(Long cardId, String kind, String finger, MultipartFile file) {
        return null;
    }

    @Override
    public ServiceResult getFingerprintImpressions(Long cardId) {
        FingerprintCardEntity card = fingerprintCardRepository.findById(cardId)
                .orElseThrow(() -> new BadRequestException("Fingerprint card not found"));

        List<PhotoResponse> photoResponses = getFingerImageResponse(card, Boolean.TRUE);
        return ServiceResult.ok(photoResponses);
    }

    @Override
    public ServiceResult deleteFingerPrint(Long id) {
        FingerprintCardEntity card = fingerprintCardRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionConstants.FINGER_PRINT_NOT_FOUND));

        fingerprintImpressionRepository.deleteByFingerprintCardId(card.getId());
        fingerprintCardRepository.deleteById(id);

        return ServiceResult.ok();
    }

    private String getKindByFingerType(String finger) {
        switch (finger) {
            case "RIGHT_THUMB":
            case "RIGHT_INDEX":
            case "RIGHT_MIDDLE":
            case "RIGHT_RING":
            case "RIGHT_LITTLE":
            case "LEFT_THUMB":
            case "LEFT_INDEX":
            case "LEFT_MIDDLE":
            case "LEFT_RING":
            case "LEFT_LITTLE":
                return FingerKind.PLAIN_SINGLE.name();
            case "LEFT_FOUR":
                return FingerKind.PLAIN_LEFT_FOUR.name();
            case "RIGHT_FOUR":
                return FingerKind.PLAIN_RIGHT_FOUR.name();
            case "LEFT_FULL":
                return FingerKind.PLAIN_LEFT_FULL.name();
            case "RIGHT_FULL":
                return FingerKind.PLAIN_RIGHT_FULL.name();
                default:
                throw new IllegalArgumentException("Invalid finger type: " + finger);
        }
    }

    private FingerprintCardResponse convertToResponse(FingerprintCardEntity card, DetaineeEntity person, Boolean isGetLink) {
        FingerprintCardResponse response = new FingerprintCardResponse();
        response.setId(card.getId());
        response.setDetaineeId(card.getPersonId());
        response.setDetaineeName(person != null ? person.getFullName() : null);
        response.setDetaineeCode(person != null ? person.getDetaineeCode() : null);
        response.setCreatedDate(card.getCreatedDate());
        response.setCreatedPlace(card.getCreatedPlace());
        response.setDp(card.getDp());
        response.setTw(card.getTw());
        response.setFpFormula(card.getFpFormula());
        response.setReasonNote(card.getReasonNote());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());

        List<PhotoResponse> fingerprintImages = getFingerImageResponse(card, isGetLink);
        response.setFingerPrintImages(fingerprintImages);

        return response;
    }

    @NotNull
    private List<PhotoResponse> getFingerImageResponse(FingerprintCardEntity card, Boolean isGetLink) {
        List<FingerprintImpressionEntity> fingers = fingerprintImpressionRepository.findByFingerprintCardId(card.getId());
        List<PhotoResponse> fingerprintImages = new ArrayList<>();
        for (FingerprintImpressionEntity item : fingers) {
            PhotoResponse photoResponse = new PhotoResponse();
            BeanUtils.copyProperties(item, photoResponse);
            if (isGetLink != null && isGetLink) {
                photoResponse.setLinkUrl(minioService.getFileUrl(photoResponse.getObjectUrl(), photoResponse.getBucket(), MinioService.DownloadOption.builder().isPublic(false).build()));
            }
            fingerprintImages.add(photoResponse);
        }
        return fingerprintImages;
    }

    private FingerprintImpressionResponse convertImpressionToResponse(FingerprintImpressionEntity impression) {
        FingerprintImpressionResponse response = new FingerprintImpressionResponse();
        response.setId(impression.getId());
        response.setFingerprintCardId(impression.getFingerprintCardId());
        response.setFinger(impression.getFinger());
        response.setKind(impression.getKind());
        response.setBucket(impression.getBucket());
        response.setImageKey(impression.getImageKey());
        response.setObjectUrl(impression.getObjectUrl());
        response.setQualityScore(impression.getQualityScore());
        response.setCapturedAt(impression.getCapturedAt());
        return response;
    }
}
