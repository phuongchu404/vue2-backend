package vn.mk.eid.web.service;


import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryUnitRequest;

/**
 * @author mk
 * @date 06-Aug-2025
 */
public interface UnitService {
    ServiceResult findAll(QueryUnitRequest request);
}
