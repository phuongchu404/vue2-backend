package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;
import vn.mk.eid.common.dao.repository.DepartmentRepository;
import vn.mk.eid.common.dao.repository.DetentionCenterRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.common.util.StringUtil;
import vn.mk.eid.web.dto.request.department.DepartmentSaveRequest;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;
import vn.mk.eid.web.dto.response.DepartmentResponse;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.DepartmentRepositoryCustom;
import vn.mk.eid.web.dto.response.DepartmentResponse;
import vn.mk.eid.web.service.DepartmentService;
import vn.mk.eid.web.service.SequenceService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentRepositoryCustom departmentRepositoryCustom;
    private final SequenceService sequenceService;
    private final DetentionCenterRepository detentionCenterRepository;

    @Override
    public ServiceResult getWithPaging(QueryDepartmentRequest request) {
        Pageable pageable = null;
        if (request.getPageNo() != null && request.getPageSize() != null) {
			pageable = PageRequest.of(request.getPageNo() - 1, request.getPageSize(), Sort.by(Sort.Direction.DESC, "name"));
        }

        Page<DepartmentResponse> page = departmentRepositoryCustom.getWithPaging(request, pageable);
        return ServiceResult.ok(page);
    }

    @Override
    public ServiceResult createDepartment(DepartmentSaveRequest request) {
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(request.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        if (StringUtil.isNotBlank(request.getCode())) {
            Optional<DepartmentEntity> optionalDepartment = departmentRepository.findByCodeAndDetentionCenterId(request.getCode(), request.getDetentionCenterId());
            if (optionalDepartment.isPresent()) {
                throw new BadRequestException(ExceptionConstants.DUPLICATE_DEPARTMENT_CODE);
            }
        }

        DepartmentEntity department = new DepartmentEntity();
        BeanUtils.copyProperties(request, department);
        if (StringUtil.isBlank(request.getCode())) {
            department.setCode(sequenceService.genCode(Constants.CodePrefix.DEPARTMENT));
        }
        departmentRepository.save(department);

        log.info("[DEPARTMENT] created: {}", department.getCode());
        DepartmentResponse response = convertToResponse(department, detentionCenter);
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult updateDepartment(DepartmentSaveRequest request, Integer id) {
        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND));

        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(request.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        BeanUtils.copyProperties(request, department);
        departmentRepository.save(department);

        log.info("[DEPARTMENT] updated: {}", department.getCode());
        DepartmentResponse response = convertToResponse(department, detentionCenter);
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult deleteDepartment(Integer id) {
        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND));

        departmentRepository.delete(department);
        log.info("[DEPARTMENT] deleted: {}", department.getCode());
        return ServiceResult.ok();
    }

    @Override
    public ServiceResult getDetailById(Integer id) {
        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND));
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(department.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        DepartmentResponse response = convertToResponse(department, detentionCenter);
        return ServiceResult.ok(response);
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

    @NotNull
    private static DepartmentResponse convertToResponse(DepartmentEntity department, DetentionCenterEntity detentionCenter) {
        DepartmentResponse response = BeanMapper.copy(department, DepartmentResponse.class);
        response.setDetentionCenterCode(detentionCenter.getCode());
        response.setDetentionCenterName(detentionCenter.getName());
        return response;
    }
}
