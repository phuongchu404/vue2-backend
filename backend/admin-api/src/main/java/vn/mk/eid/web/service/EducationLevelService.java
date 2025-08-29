package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryEducationLevelRequest;

public interface EducationLevelService {
    ServiceResult getWithPaging(QueryEducationLevelRequest request);
}
