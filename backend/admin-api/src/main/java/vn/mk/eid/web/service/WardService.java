package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;

public interface WardService {
    ServiceResult findAll(QueryWardRequest request);

    ServiceResult findByCode(String code);

    ServiceResult findByProvinceCode(String provinceCode);
}
