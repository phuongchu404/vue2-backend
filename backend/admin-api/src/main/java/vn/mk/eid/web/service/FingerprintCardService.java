package vn.mk.eid.web.service;

import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.FingerprintCardCreateRequest;

public interface FingerprintCardService {
    ServiceResult createFingerprintCard(FingerprintCardCreateRequest request);

    ServiceResult getFingerprintCardByDetaineeId(Long detaineeId);

    ServiceResult getFingerprintCardById(Long id);

    ServiceResult uploadFingerprintImpression(Long cardId, String kind, String finger, MultipartFile file);

    ServiceResult getFingerprintImpressions(Long cardId);
}
