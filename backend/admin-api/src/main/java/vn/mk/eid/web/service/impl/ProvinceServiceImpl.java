package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.ProvinceEntity;
import vn.mk.eid.common.dao.repository.ProvinceRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryProvinceRequest;
import vn.mk.eid.web.dto.response.ProvinceResponse;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.service.ProvinceService;
import vn.mk.eid.web.specification.ProvinceSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public ServiceResult findAll(QueryProvinceRequest request) {
        List<ProvinceResponse> provinceResponses = provinceRepository
                .findAll(ProvinceSpecification.getProvinceSpecification(request))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ServiceResult.ok(provinceResponses);
    }

    @Override
    public ServiceResult findProvinceByCode(String code) {
        ProvinceEntity province = provinceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Province not found with code: " + code));
        return ServiceResult.ok(convertToResponse(province));
    }

    private ProvinceResponse convertToResponse(ProvinceEntity province) {
        ProvinceResponse response = new ProvinceResponse();
        response.setCode(province.getCode());
        response.setName(province.getName());
        response.setFullName(province.getFullName());
        response.setCodeName(province.getCodeName());
        return response;
    }
}
