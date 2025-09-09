package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.identity_record.QueryIdentityRecordRequest;
import vn.mk.eid.web.dto.response.IdentityRecordResponse;

public interface IdentityRecordRepositoryCustom {
    Page<IdentityRecordResponse> getWithPaging(QueryIdentityRecordRequest request, Pageable pageable);
}
