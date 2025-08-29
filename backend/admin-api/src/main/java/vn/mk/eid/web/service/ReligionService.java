package vn.mk.eid.web.service;

import lombok.Data;
import vn.mk.eid.common.data.ServiceResult;

public interface ReligionService {
    ServiceResult findAll(String keyword);
}
