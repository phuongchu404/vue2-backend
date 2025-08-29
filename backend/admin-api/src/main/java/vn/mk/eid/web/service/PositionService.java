package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryPositionRequest;

public interface PositionService {
    ServiceResult getWithPaging(QueryPositionRequest request);
}
