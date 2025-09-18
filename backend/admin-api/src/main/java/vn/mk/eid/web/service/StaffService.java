package vn.mk.eid.web.service;

import org.springframework.data.domain.Pageable;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.request.staff.StaffCreateRequest;
import vn.mk.eid.web.dto.request.staff.StaffUpdateRequest;

public interface StaffService {
    ServiceResult getStaffWithPaging(QueryStaffRequest request, Pageable pageable);

    ServiceResult createStaff(StaffCreateRequest request);

    ServiceResult updateStaff(Integer id, StaffUpdateRequest request);

    ServiceResult deleteStaff(Integer id);

    ServiceResult getStaffById(Integer id);

    ServiceResult findTop3NewestStaffs();

    ServiceResult countStaffs();
}
