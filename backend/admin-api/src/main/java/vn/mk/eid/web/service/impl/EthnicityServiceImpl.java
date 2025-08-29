package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.EthnicityEntity;
import vn.mk.eid.common.dao.repository.EthnicityRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.response.EthnicityResponse;
import vn.mk.eid.web.service.EthnicityService;
import vn.mk.eid.web.specification.EthnicitySpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EthnicityServiceImpl implements EthnicityService {

    private final EthnicityRepository ethnicityRepository;

    @Override
    public ServiceResult findAll(String keyword) {
        List<EthnicityResponse> result = ethnicityRepository
                .findAll(EthnicitySpecification.getEthnicityEntitySpecification(keyword))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ServiceResult.ok(result);
    }

    private EthnicityResponse convertToResponse(EthnicityEntity entity) {
        EthnicityResponse ethnicityResponse = new EthnicityResponse();
        ethnicityResponse.setId(entity.getId());
        ethnicityResponse.setName(entity.getName());
        ethnicityResponse.setCode(entity.getCode());
        return ethnicityResponse;
    }
}
