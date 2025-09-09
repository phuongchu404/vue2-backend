package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;
import vn.mk.eid.web.dto.response.DepartmentResponse;

public interface DepartmentRepositoryCustom {
    Page<DepartmentResponse> getWithPaging(QueryDepartmentRequest request, Pageable pageable);
}
