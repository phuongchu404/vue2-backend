package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryDepartmentRequest;

public interface DepartmentService {
    ServiceResult getWithPaging(QueryDepartmentRequest request);
}
