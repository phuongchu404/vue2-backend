package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import vn.mk.eid.web.dto.response.FingerprintCardResponse;
import vn.mk.eid.web.dto.response.FingerprintImpressionResponse;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.service.FingerprintCardService;
import vn.mk.eid.web.service.MinioService;
import vn.mk.eid.web.utils.FileUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FingerprintCardServiceImpl implements FingerprintCardService {

    private final FingerprintCardRepository fingerprintCardRepository;

    private final FingerprintImpressionRepository fingerprintImpressionRepository;

    private final DetaineeRepository detaineeRepository;

    private final MinioService minioService;

    @Override
    public ServiceResult createFingerprintCard(FingerprintCardCreateRequest request) {
        DetaineeEntity person = detaineeRepository.findById(request.getDetaineeId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        Integer detentionCenterId = person.getDetentionCenterId();
        String detaineeCode = person.getDetaineeCode();
        String idNumber = person.getIdNumber();

        FingerprintCardEntity card = new FingerprintCardEntity();
        card.setPersonId(person.getId());
        card.setCreatedDate(LocalDate.now());
        card.setCreatedPlace(request.getCreatedPlace());
        card.setDp(request.getDp());
        card.setTw(request.getTw());
        card.setFpFormula(request.getFpFormula());
        card.setReasonNote(request.getReasonNote());

        card = fingerprintCardRepository.save(card);
        Long fingerprintCardId = card.getId();

        List<FingerprintImpressionEntity> impressions = new ArrayList<>();

        if(request.getFingerprintImages() != null && !request.getFingerprintImages().isEmpty()) {
            Map<String, MultipartFile> images = request.getFingerprintImages();
            for (Map.Entry<String, MultipartFile> entry : images.entrySet()) {
                String imageKey = entry.getKey();
                MultipartFile imageFile = entry.getValue();

                if (imageFile != null && !imageFile.isEmpty()) {
                    // upload image to MinIO
                    String extensionFile = FileUtil.getExtensionOfFile(imageFile.getOriginalFilename());
                    String fileName = WebConstants.Fingerprint.FINGERPRINT_PREFIX + fingerprintCardId + WebConstants.CommonSymbol.SHIFT_DASH +
                            detaineeCode + WebConstants.CommonSymbol.SHIFT_DASH + imageKey;
                    log.info("Uploading fingerprint for identity record: {}, view: {}, fileName: {}", fingerprintCardId, imageKey, fileName);
                    String dir = detentionCenterId + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.DETAINEE + WebConstants.CommonSymbol.FORWARD_SLASH +
                            detaineeCode + WebConstants.CommonSymbol.DASH + idNumber  + WebConstants.CommonSymbol.FORWARD_SLASH + WebConstants.bucketMinio.IDENTITY;
                    log.info("Uploading fingerprint to directory: {}", dir);
                    Pair<String, String> uploadData = minioService.uploadFile(imageFile, fileName, dir);

                    FingerprintImpressionEntity impression = new FingerprintImpressionEntity();
                    impression.setFingerprintCardId(fingerprintCardId);
                    impression.setFinger(imageKey);
                    impression.setKind(getKindByFingerType(imageKey));
                    impression.setBucket(dir);
                    impression.setObjectUrl(uploadData.getLeft());
                    impression.setImageKey(fileName);

                    impressions.add(impression);
                }
            }
            fingerprintImpressionRepository.saveAll(impressions);
        }
        log.info("Created fingerprint card for person: {}", person.getDetaineeCode());
        return ServiceResult.ok(convertToResponse(card,person));
    }

    @Override
    public ServiceResult getFingerprintCardByDetaineeId(Long detaineeId) {
        DetaineeEntity person = detaineeRepository.findById(detaineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        FingerprintCardEntity card = fingerprintCardRepository.findByDetaineeId(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        return ServiceResult.ok(convertToResponse(card, person));
    }

    @Override
    public ServiceResult getFingerprintCardById(Long id) {
        FingerprintCardEntity card = fingerprintCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fingerprint card not found"));
        return ServiceResult.ok(convertToResponse(card));
    }

    @Override
    public ServiceResult uploadFingerprintImpression(Long cardId, String kind, String finger, MultipartFile file) {
        return null;
    }

    @Override
    public ServiceResult getFingerprintImpressions(Long cardId) {
        return null;
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
    private FingerprintCardResponse convertToResponse(FingerprintCardEntity card) {
        FingerprintCardResponse response = new FingerprintCardResponse();
        response.setId(card.getId());
        response.setCreatedDate(card.getCreatedDate());
        response.setCreatedPlace(card.getCreatedPlace());
        response.setDp(card.getDp());
        response.setTw(card.getTw());
        response.setFpFormula(card.getFpFormula());
        response.setReasonNote(card.getReasonNote());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
    }

    private FingerprintCardResponse convertToResponse(FingerprintCardEntity card, DetaineeEntity person) {
        FingerprintCardResponse response = new FingerprintCardResponse();
        response.setId(card.getId());
        response.setPersonId(card.getPersonId());
        response.setPersonName(person.getFullName());
        response.setPersonCode(person.getDetaineeCode());
        response.setCreatedDate(card.getCreatedDate());
        response.setCreatedPlace(card.getCreatedPlace());
        response.setDp(card.getDp());
        response.setTw(card.getTw());
        response.setFpFormula(card.getFpFormula());
        response.setReasonNote(card.getReasonNote());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
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
