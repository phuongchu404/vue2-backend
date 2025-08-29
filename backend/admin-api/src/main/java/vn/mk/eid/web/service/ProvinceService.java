package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryProvinceRequest;

public interface ProvinceService {
    ServiceResult findAll(QueryProvinceRequest request);

    ServiceResult findProvinceByCode(String code);
}
