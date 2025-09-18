package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;
import vn.mk.eid.common.dao.entity.ProvinceEntity;
import vn.mk.eid.common.dao.entity.WardEntity;
import vn.mk.eid.common.dao.repository.DetentionCenterRepository;
import vn.mk.eid.common.dao.repository.ProvinceRepository;
import vn.mk.eid.common.dao.repository.WardRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.web.dto.request.DetentionCenterSearchRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterCreateRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterUpdateRequest;
import vn.mk.eid.web.dto.request.detention_center.QueryDetentionCenterRequest;
import vn.mk.eid.web.dto.response.DetentionCenterResponse;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.service.DetentionCenterService;
import vn.mk.eid.web.service.SequenceService;
import vn.mk.eid.web.specification.DetentionCenterSpecification;
import vn.mk.eid.web.utils.StringUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetentionCenterServiceImpl implements DetentionCenterService {
    private final DetentionCenterRepository detentionCenterRepository;
    private final SequenceService sequenceService;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    @Override
    public ServiceResult createDetentionCenter(DetentionCenterCreateRequest request) {
        DetentionCenterEntity center = new DetentionCenterEntity();
        BeanUtils.copyProperties(request, center);
        if (StringUtil.isBlank(center.getCode())) {
            center.setCode(sequenceService.genCode(Constants.CodePrefix.DETENTION_CENTER));
        }
        center.setCurrentPopulation(request.getCurrentPopulation());
        center.setIsActive(true);

        center = detentionCenterRepository.save(center);
        log.info("Created detention center: {}", center.getName());
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult findAllDetentionCenters() {
            List<DetentionCenterEntity> detentions = detentionCenterRepository.findAll();
            return ServiceResult.ok(detentions
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult findDetentionCenterById(Integer id) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult updateDetentionCenter(Integer id, DetentionCenterUpdateRequest request) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        // duplicate code
        if (Objects.equals(request.getCode(), center.getCode())) {
            detentionCenterRepository.findByCode(request.getCode())
                    .ifPresent(detentionCenter -> { throw new BadRequestException(ExceptionConstants.DUPLICATE_DETENTION_CENTER_CODE); });
        }
        center.setCurrentPopulation(request.getCurrentPopulation());

        // capacity < current_population
        if (request.getCapacity() != null && request.getCapacity() < center.getCurrentPopulation()) {
            throw new BadRequestException(ExceptionConstants.DETENTION_CENTER_CAPACITY_INVALID);
        }

        BeanUtils.copyProperties(request, center);
        center = detentionCenterRepository.save(center);
        log.info("Updated detention center: {}", center.getName());
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult deleteDetentionCenter(Integer id) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        detentionCenterRepository.delete(center);
        log.info("Deleted detention center: {}", center.getName());
        return ServiceResult.ok("Detention center deleted successfully");
    }

    @Override
    public ServiceResult findDetentionCentersByProvinceCode(String provinceCode) {
        return ServiceResult.ok(detentionCenterRepository.findByProvinceId(provinceCode).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult searchDetentionCenters(DetentionCenterSearchRequest request, Pageable pageable) {
        if(request.getCode() == "" ) request.setCode(null);
        if(request.getName() == "" ) request.setName(null);
        Page<DetentionCenterEntity> page = detentionCenterRepository.searchDetentionCenters(
                request.getCode(),
                request.getName(),
                request.getIsActive(),
                pageable
        );
        return ServiceResult.ok(page.map(this::convertToResponse));
    }

    @Override
    public ServiceResult getTop3Newest() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<DetentionCenterEntity> page = detentionCenterRepository.findTop3OrderByUpdateAt(pageRequest);
        return ServiceResult.ok(page.stream().map(this::convertToResponse).collect(Collectors.toList()));
    }

    @Override
    public ServiceResult countDetentionCenters() {
        Long count = detentionCenterRepository.countByIsActiveTrue();
        return ServiceResult.ok(count);
    }

    private DetentionCenterResponse convertToResponse(DetentionCenterEntity center) {
        DetentionCenterResponse response = new DetentionCenterResponse();
        response.setId(center.getId());
        response.setName(center.getName());
        response.setCode(center.getCode());
        response.setAddress(center.getAddress());

        if(center.getProvinceId() != null){
            ProvinceEntity province = provinceRepository.findById(center.getProvinceId()).orElse(null);
            response.setProvinceFullName(province.getFullName());
            response.setProvinceId(center.getProvinceId());
        }

        if(center.getWardId() != null){
            WardEntity ward = wardRepository.findById(center.getWardId()).orElse(null);
            response.setWardFullName(ward.getFullName());
            response.setWardId(center.getWardId());
        }

        response.setPhone(center.getPhone());
        response.setEmail(center.getEmail());
        response.setDirector(center.getDirector());
        response.setDeputyDirector(center.getDeputyDirector());
        response.setEstablishedDate(center.getEstablishedDate());
        response.setCapacity(center.getCapacity());
        response.setCurrentPopulation(center.getCurrentPopulation());
        response.setIsActive(center.getIsActive());
        response.setCreatedAt(center.getCreatedAt());
        response.setUpdatedAt(center.getUpdatedAt());
        return response;
    }
}
