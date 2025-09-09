package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.AdministrativeUnitEntity;
import vn.mk.eid.common.dao.repository.AdministrativeUnitRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.web.dto.request.administrative_unit.QueryAdministrativeUnitRequest;
import vn.mk.eid.web.dto.response.AdministrativeUnitResponse;
import vn.mk.eid.web.service.AdministrativeUnitService;
import vn.mk.eid.web.specification.AdminUnitSpecification;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdministrativeServiceImpl implements AdministrativeUnitService {

    private final AdministrativeUnitRepository administrativeUnitRepository;

    @Override
    public ServiceResult getAll(QueryAdministrativeUnitRequest request) {
        List<AdministrativeUnitEntity> units = administrativeUnitRepository
                .findAll(AdminUnitSpecification.filterByKeyword(request));
        List<AdministrativeUnitResponse> responses = BeanMapper.listCopy(units, AdministrativeUnitResponse.class);
        return ServiceResult.ok(responses);
    }
}
