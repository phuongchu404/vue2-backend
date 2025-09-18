package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.*;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.common.data.Paging;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.Gender;
import vn.mk.eid.web.constant.StaffStatus;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.request.staff.StaffCreateRequest;
import vn.mk.eid.web.dto.request.staff.StaffUpdateRequest;
import vn.mk.eid.web.dto.response.StaffResponse;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.StaffRepositoryCustom;
import vn.mk.eid.web.service.RedisService;
import vn.mk.eid.web.service.SequenceService;
import vn.mk.eid.web.service.StaffService;
import vn.mk.eid.web.utils.StringUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final StaffRepositoryCustom staffRepositoryCustom;
    private final DetentionCenterRepository detentionCenterRepository;
    private final DepartmentRepository departmentRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final PositionRepository positionRepository;
    private final EthnicityRepository ethnicityRepository;
    private final ReligionRepository religionRepository;
    private final RedisService redisService;
    private final SequenceService sequenceService;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    @Override
    public ServiceResult getStaffWithPaging(QueryStaffRequest request, Pageable pageable) {
        Page<StaffResponse> page = staffRepositoryCustom.getWithPaging(request, pageable);
        return ServiceResult.ok(page);
    }

    @Override
    public ServiceResult createStaff(StaffCreateRequest request) {
        DetentionCenterEntity detentionCenterEntity = detentionCenterRepository.findById(request.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
        validateStaffRequest(request, null);
        String staffCode = sequenceService.genCode(Constants.CodePrefix.STAFF_CODE);

        StaffEntity staffEntity = new StaffEntity();
        BeanUtils.copyProperties(request, staffEntity);

        staffEntity.setStaffCode(staffCode);
        staffEntity.setGender(request.getGender());
        staffEntity.setStatus(StaffStatus.ACTIVE.name());
        staffEntity.setIsActive(Boolean.TRUE);

        staffEntity = staffRepository.save(staffEntity);
        log.info("[STAFF] Staff created");
        return ServiceResult.ok(convertToStaffResponse(staffEntity, detentionCenterEntity, Boolean.FALSE));
    }

    @Override
    public ServiceResult updateStaff(Integer id, StaffUpdateRequest request) {
        StaffEntity staffEntity = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.STAFF_NOT_FOUND));
        validateStaffRequest(request, id);

        BeanUtils.copyProperties(request, staffEntity);
        if (request.getGender() != null) {
            staffEntity.setGender(request.getGender());
        }
        staffRepository.save(staffEntity);
        log.info("[STAFF] Staff updated");
        return ServiceResult.ok(convertToStaffResponse(staffEntity, null, Boolean.FALSE));
    }

    @Override
    public ServiceResult deleteStaff(Integer id) {
        StaffEntity staffEntity = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.STAFF_NOT_FOUND));
        staffRepository.delete(staffEntity);
        log.info("[STAFF] Staff deleted");
        return ServiceResult.ok();
    }

    @Override
    public ServiceResult getStaffById(Integer id) {
        StaffEntity staffEntity = staffRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.STAFF_NOT_FOUND));
        return ServiceResult.ok(convertToStaffResponse(staffEntity, null, Boolean.TRUE));
    }

    @Override
    public ServiceResult findTop3NewestStaffs() {
        Pageable pageable = Pageable.ofSize(3);
        List<StaffEntity> staffs = staffRepository.findTop3ByOrderByUpdateAtDesc(pageable);
        return ServiceResult.ok(staffs.stream().map(this::convertToStaffResponse).collect(Collectors.toList()));
    }

    @Override
    public ServiceResult countStaffs() {
        Long count = staffRepository.countByIsActiveTrue();
        return ServiceResult.ok(count);
    }

    private StaffResponse convertToStaffResponse(StaffEntity staffEntity, DetentionCenterEntity detentionCenterEntity, Boolean isGetFullData) {
        StaffResponse staffResponse = new StaffResponse();
        BeanUtils.copyProperties(staffEntity, staffResponse);
        staffResponse.setGender(staffEntity.getGender());

        // detentionCenter Info
        if (detentionCenterEntity == null) {
            detentionCenterEntity = detentionCenterRepository.findById(staffEntity.getDetentionCenterId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
        }
        staffResponse.setDetentionCenterCode(detentionCenterEntity.getCode());
        staffResponse.setDetentionCenterName(detentionCenterEntity.getName());

        if (!Objects.equals(isGetFullData, Boolean.TRUE)) {
            return staffResponse;
        }

        // department Info
        if (staffEntity.getDepartmentId() != null) {
            Optional<DepartmentEntity> optionalDepartment = departmentRepository.findById(staffEntity.getDepartmentId());
            if (optionalDepartment.isPresent()) {
                DepartmentEntity departmentEntity = optionalDepartment.get();
                staffResponse.setDepartmentCode(departmentEntity.getCode());
                staffResponse.setDepartmentName(departmentEntity.getName());
            }
        }

        // educationLevel Info
        if (staffEntity.getEducationLevelId() != null) {
            Optional<EducationLevelEntity> optionalEducationLevel = educationLevelRepository.findById(staffEntity.getEducationLevelId());
            optionalEducationLevel.ifPresent(educationLevelEntity -> staffResponse.setEducationLevelName(educationLevelEntity.getName()));
        }

        // positon Info
        if (staffEntity.getPositionId() != null) {
            Optional<PositionEntity> optionalPosition = positionRepository.findById(staffEntity.getPositionId());
            optionalPosition.ifPresent(positionEntity -> staffResponse.setPositionName(positionEntity.getName()));
        }

        // ethnicity Info
        if (staffEntity.getEthnicityId() != null) {
            Optional<EthnicityEntity> optionalEthnicity = ethnicityRepository.findById(staffEntity.getEthnicityId());
            optionalEthnicity.ifPresent(ethnicityEntity -> staffResponse.setEthnicityName(ethnicityEntity.getName()));
        }

        // region Info
        if (staffEntity.getReligionId() != null) {
            Optional<ReligionEntity> optionalReligion = religionRepository.findById(staffEntity.getReligionId());
            optionalReligion.ifPresent(religionEntity -> staffResponse.setReligionName(religionEntity.getName()));
        }

        if(staffEntity.getPermanentWardId() != null) {
            Optional<WardEntity> optionalWard = wardRepository.findById(staffEntity.getPermanentWardId());
            optionalWard.ifPresent(wardEntity -> {
                staffResponse.setPermanentWardId(wardEntity.getCode());
                staffResponse.setPermanentProvinceId(wardEntity.getProvince().getCode());
            });
        }
        if(staffEntity.getTemporaryWardId() != null) {
            Optional<WardEntity> optionalWard = wardRepository.findById(staffEntity.getTemporaryWardId());
            optionalWard.ifPresent(wardEntity -> {
                staffResponse.setTemporaryWardId(wardEntity.getCode());
                staffResponse.setTemporaryProvinceId(wardEntity.getProvince().getCode());
            });
        }
        return staffResponse;
    }

    private StaffResponse convertToStaffResponse(StaffEntity staffEntity) {
        StaffResponse staffResponse = new StaffResponse();
        staffResponse.setStaffCode(staffEntity.getStaffCode());
        staffResponse.setFullName(staffEntity.getFullName());
        staffResponse.setGender(staffEntity.getGender());
        staffResponse.setIdNumber(staffEntity.getIdNumber());
        staffResponse.setRank(staffEntity.getRank());
        staffResponse.setProfileNumber(staffEntity.getProfileNumber());
        return staffResponse;
    }

    private void validateStaffRequest(StaffCreateRequest request, Integer id) {
        if (request.getEthnicityId() != null) {
            ethnicityRepository.findById(request.getEthnicityId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.ETHNICITY_NOT_FOUND));
        }

        if (request.getPositionId() != null) {
            positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.POSITION_NOT_FOUND));
        }

        if (request.getDepartmentId() != null && request.getDetentionCenterId() != null) {
            departmentRepository.findByIdAndDetentionCenterId(request.getDepartmentId(), request.getDetentionCenterId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND));
        }

        if (request.getEducationLevelId() != null) {
            educationLevelRepository.findById(request.getEducationLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.EDUCATION_LEVEL_NOT_FOUND));
        }

        if (StringUtil.isNotBlank(request.getIdNumber())) {
            Optional<StaffEntity> optionalStaff;
            if (id == null) {
                optionalStaff = staffRepository.findByIdNumber(request.getIdNumber());
            } else {
                optionalStaff = staffRepository.findByIdNumberAndIdNot(request.getIdNumber(), id);
            }
            if (optionalStaff.isPresent()) {
                throw new BadRequestException(ExceptionConstants.DUPLICATE_ID_NUMBER);
            }
        }
    }
}
