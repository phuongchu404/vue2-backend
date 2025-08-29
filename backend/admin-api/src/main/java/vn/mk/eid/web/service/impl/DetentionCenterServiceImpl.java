package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;
import vn.mk.eid.common.dao.repository.DetentionCenterRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.DetentionCenterCreateRequest;
import vn.mk.eid.web.dto.request.DetentionCenterSearchRequest;
import vn.mk.eid.web.dto.request.DetentionCenterUpdateRequest;
import vn.mk.eid.web.dto.response.DetentionCenterResponse;
import vn.mk.eid.web.service.DetentionCenterService;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetentionCenterServiceImpl implements DetentionCenterService {

    private final DetentionCenterRepository detentionCenterRepository;

    @Override
    public ServiceResult createDetentionCenter(DetentionCenterCreateRequest request) {
        DetentionCenterEntity center = new DetentionCenterEntity();
        center.setName(request.getName());
        center.setCode(request.getCode());
        center.setAddress(request.getAddress());
        center.setWardId(request.getWardId());
        center.setProvinceId(request.getProvinceId());
        center.setPhone(request.getPhone());
        center.setEmail(request.getEmail());
        center.setDirector(request.getDirector());
        center.setDeputyDirector(request.getDeputyDirector());
        center.setEstablishedDate(request.getEstablishedDate());
        center.setCapacity(request.getCapacity());
        center.setCurrentPopulation(0);
        center.setIsActive(true);

        center = detentionCenterRepository.save(center);
        log.info("Created detention center: {}", center.getName());
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult findAllDetentionCenters() {
        return ServiceResult.ok(detentionCenterRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult findDetentionCenterById(Integer id) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detention center not found with id: " + id));
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult updateDetentionCenter(Integer id, DetentionCenterUpdateRequest request) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detention center not found with id: " + id));

        center.setName(request.getName());
        center.setCode(request.getCode());
        center.setAddress(request.getAddress());
        center.setWardId(request.getWardId());
        center.setProvinceId(request.getProvinceId());
        center.setPhone(request.getPhone());
        center.setEmail(request.getEmail());
        center.setDirector(request.getDirector());
        center.setDeputyDirector(request.getDeputyDirector());
        center.setEstablishedDate(request.getEstablishedDate());
        center.setCapacity(request.getCapacity());

        center = detentionCenterRepository.save(center);
        log.info("Updated detention center: {}", center.getName());
        return ServiceResult.ok(convertToResponse(center));
    }

    @Override
    public ServiceResult deleteDetentionCenter(Integer id) {
        DetentionCenterEntity center = detentionCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detention center not found with id: " + id));

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

    private DetentionCenterResponse convertToResponse(DetentionCenterEntity center) {
        DetentionCenterResponse response = new DetentionCenterResponse();
        response.setId(center.getId());
        response.setName(center.getName());
        response.setCode(center.getCode());
        response.setAddress(center.getAddress());
        response.setWardId(center.getWardId());
        response.setProvinceId(center.getProvinceId());
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
