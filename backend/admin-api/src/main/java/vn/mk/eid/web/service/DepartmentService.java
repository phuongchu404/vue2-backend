package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.department.DepartmentSaveRequest;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;

public interface DepartmentService {
    ServiceResult getWithPaging(QueryDepartmentRequest request);
    ServiceResult createDepartment(DepartmentSaveRequest request);
    ServiceResult updateDepartment(DepartmentSaveRequest request, Integer id);
    ServiceResult deleteDepartment(Integer id);
    ServiceResult getDetailById(Integer id);
    ServiceResult getByDententionCenterId(Integer dententionCenterId);
}
