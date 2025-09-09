package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.administrative_region.QueryAdministrativeRegionRequest;

public interface AdministrativeRegionService {
    ServiceResult getAll(QueryAdministrativeRegionRequest request);
}
