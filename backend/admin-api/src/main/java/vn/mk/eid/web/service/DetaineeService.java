package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.DetaineeCreateRequest;
import vn.mk.eid.web.dto.request.DetaineeUpdateRequest;

import java.util.Date;

public interface DetaineeService {
    ServiceResult createDetainee(DetaineeCreateRequest request);

    ServiceResult updateDetainee(Long id, DetaineeUpdateRequest request);

    ServiceResult getDetainee(Long id);

    ServiceResult getDetaineeByCode(String code);

    ServiceResult getAllDetainees(Pageable pageable);

    ServiceResult searchDetainees(String keyword, Pageable pageable);

    ServiceResult getDetaineeByStatus(String status, Pageable pageable);

    ServiceResult getDetaineeByCenter(Integer centerId, Pageable pageable);

//    ServiceResult releaseDetainee(Long id, Date releaseDate, String reason);

    ServiceResult deleteDetainee(Long id);


}
