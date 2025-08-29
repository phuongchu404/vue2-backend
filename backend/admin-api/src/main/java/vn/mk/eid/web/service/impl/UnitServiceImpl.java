package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.UnitEntity;
import vn.mk.eid.common.dao.repository.UnitRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.web.dto.request.QueryUnitRequest;
import vn.mk.eid.web.dto.response.UnitResponse;
import vn.mk.eid.web.service.UnitService;
import vn.mk.eid.web.specification.UnitSpecification;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;

    @Override
    public ServiceResult findAll(QueryUnitRequest request) {
        List<UnitResponse> unitResponses = unitRepository
                .findAll(UnitSpecification.getUnitSpecification(request))
                .stream()
                .map(this::convertToUnitResponse)
                .collect(Collectors.toList());
        return ServiceResult.ok(unitResponses);
    }

    private UnitResponse convertToUnitResponse(UnitEntity unit) {
        UnitResponse unitResponse = new UnitResponse();
        unitResponse.setCode(unit.getUnitCode());
        unitResponse.setName(unit.getUnitName());
        return unitResponse;
    }
}
