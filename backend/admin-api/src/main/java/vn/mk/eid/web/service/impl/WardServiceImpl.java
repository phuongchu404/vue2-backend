package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.WardEntity;
import vn.mk.eid.common.dao.repository.WardRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.dto.response.WardResponse;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.service.WardService;
import vn.mk.eid.web.specification.WardSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WardServiceImpl implements WardService {

    private final WardRepository wardRepository;

    @Override
    public ServiceResult findAll(QueryWardRequest request) {
        List<WardResponse> wardResponses = wardRepository.findAll(WardSpecification.getWardSpecification(request)).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ServiceResult.ok(wardResponses);
    }

    @Override
    public ServiceResult findByCode(String code) {
        WardEntity wardEntity = wardRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with code: " + code));
        return ServiceResult.ok(convertToResponse(wardEntity));
    }

    @Override
    public ServiceResult findByProvinceCode(String provinceCode) {
        WardEntity wardEntity = wardRepository.findByProvinceCode(provinceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with province code: " + provinceCode));
        return ServiceResult.ok(convertToResponse(wardEntity));
    }

    private WardResponse convertToResponse(WardEntity entity) {
        WardResponse response = new WardResponse();
        response.setName(entity.getName());
        response.setCode(entity.getCode());
        response.setFullName(entity.getFullName());
        response.setCodeName(entity.getCodeName());
        return response;
    }
}
