package vn.mk.eid.web.service;


import vn.mk.eid.common.dao.entity.CountryEntity;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;

import java.util.List;

/**
 * @author mk
 * @date 06-Aug-2025
 */
public interface CountrySevice {
    ServiceResult getAllCountry(QueryWardRequest request);
}
