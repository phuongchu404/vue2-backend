package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.detainee.DetaineeCreateRequest;
import vn.mk.eid.web.dto.request.detainee.DetaineeUpdateRequest;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;

public interface DetaineeService {
    ServiceResult createDetainee(DetaineeCreateRequest request);

    ServiceResult updateDetainee(Long id, DetaineeUpdateRequest request);

    ServiceResult getDetainee(Long id);

    ServiceResult getDetaineeByCode(String code);

    ServiceResult getWithPaging(QueryDetaineeRequest request, Pageable pageable);

//    ServiceResult releaseDetainee(Long id, Date releaseDate, String reason);

    ServiceResult deleteDetainee(Long id);

    ServiceResult getAllNoPaging();

    ServiceResult getTop3NewestDetainees();

    ServiceResult getDetaineeCount();

}
