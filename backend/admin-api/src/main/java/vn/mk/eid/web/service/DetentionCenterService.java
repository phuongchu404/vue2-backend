package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.DetentionCenterSearchRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterCreateRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterUpdateRequest;
import vn.mk.eid.web.dto.request.detention_center.QueryDetentionCenterRequest;

public interface DetentionCenterService {
    ServiceResult createDetentionCenter(DetentionCenterCreateRequest request);

    ServiceResult findAllDetentionCenters();

    ServiceResult findDetentionCenterById(Integer id);

    ServiceResult updateDetentionCenter(Integer id, DetentionCenterUpdateRequest request);

    ServiceResult deleteDetentionCenter(Integer id);

    ServiceResult findDetentionCentersByProvinceCode(String provinceCode);

    ServiceResult searchDetentionCenters(DetentionCenterSearchRequest request, Pageable pageable);

    ServiceResult getTop3Newest();

    ServiceResult countDetentionCenters();
}

