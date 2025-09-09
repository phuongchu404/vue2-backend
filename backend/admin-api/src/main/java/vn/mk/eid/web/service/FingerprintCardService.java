package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.FingerprintCardCreateRequest;
import vn.mk.eid.web.dto.request.QueryFingerPrintRequest;

public interface FingerprintCardService {
    ServiceResult getWithPaging(Pageable pageable, QueryFingerPrintRequest request);

    ServiceResult createFingerprintCard(FingerprintCardCreateRequest request);

    ServiceResult updateFingerprintCard(FingerprintCardCreateRequest request, Long id);

    ServiceResult getFingerprintCardByDetaineeId(Long detaineeId);

    ServiceResult getFingerprintCardById(Long id);

    ServiceResult uploadFingerprintImpression(Long cardId, String kind, String finger, MultipartFile file);

    ServiceResult getFingerprintImpressions(Long cardId);

    ServiceResult deleteFingerPrint(Long id);
}
