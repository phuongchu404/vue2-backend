package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.administrative_unit.QueryAdministrativeUnitRequest;

public interface AdministrativeUnitService {
    ServiceResult getAll(QueryAdministrativeUnitRequest request);
}
