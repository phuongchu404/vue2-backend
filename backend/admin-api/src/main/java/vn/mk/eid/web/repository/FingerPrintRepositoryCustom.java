package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.QueryFingerPrintRequest;
import vn.mk.eid.web.dto.response.FingerprintCardResponse;

public interface FingerPrintRepositoryCustom {
    Page<FingerprintCardResponse> getWithPaging(Pageable pageable, QueryFingerPrintRequest request);
}
