package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.ReligionEntity;
import vn.mk.eid.common.dao.repository.ReligionRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.response.ReligionResponse;
import vn.mk.eid.web.service.ReligionService;
import vn.mk.eid.web.specification.ReligionSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReligionServiceImpl implements ReligionService {
    private final ReligionRepository religionRepository;
    @Override
    public ServiceResult findAll(String keyword) {
        List<ReligionResponse> religionResponseList = religionRepository
                .findAll(ReligionSpecification.getReligionSpecification(keyword))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ServiceResult.ok(religionResponseList);
    }

    private ReligionResponse convertToResponse(ReligionEntity entity) {
        ReligionResponse response = new ReligionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCode(entity.getCode());
        return response;
    }
}
