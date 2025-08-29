package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.QueryStaffRequest;
import vn.mk.eid.web.dto.response.StaffResponse;

public interface StaffRepositoryCustom {
    Page<StaffResponse> getWithPaging(QueryStaffRequest request, Pageable pageable);
}
