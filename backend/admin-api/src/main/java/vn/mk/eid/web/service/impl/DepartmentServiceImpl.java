package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.repository.DepartmentRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryDepartmentRequest;
import vn.mk.eid.web.dto.response.DepartmentResponse;
import vn.mk.eid.web.service.DepartmentService;
import vn.mk.eid.web.specification.DepartmentSpecification;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public ServiceResult getWithPaging(QueryDepartmentRequest request) {
        Pageable pageable = null;
        if (request.getPageNo() != null && request.getPageSize() != null) {
            pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        }
        List<DepartmentEntity> departments;
        if (pageable == null) {
            departments = departmentRepository.findAll(DepartmentSpecification.getDepartmentSpecification(request));
        } else {
            departments = departmentRepository.findAll(DepartmentSpecification.getDepartmentSpecification(request), pageable).getContent();
        }
        return ServiceResult.ok(departments);
    }

    @Override
    public ServiceResult getByDententionCenterId(Integer dententionCenterId) {
        List<DepartmentEntity> departments = departmentRepository.findByDetentionCenterIdAndIsActiveTrue(dententionCenterId);
        return ServiceResult.ok(departments.stream().map(this::convertToResponse));
    }

    private DepartmentResponse convertToResponse(DepartmentEntity departmentEntity) {
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(departmentEntity.getId());
        departmentResponse.setName(departmentEntity.getName());
        departmentResponse.setCode(departmentEntity.getCode());
        return departmentResponse;
    }
}
