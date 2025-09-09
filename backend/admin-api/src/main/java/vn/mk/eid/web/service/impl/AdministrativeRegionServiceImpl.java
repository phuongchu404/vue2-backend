package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.AdministrativeRegionEntity;
import vn.mk.eid.common.dao.repository.AdministrativeRegionRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.web.dto.request.administrative_region.QueryAdministrativeRegionRequest;
import vn.mk.eid.web.dto.response.AdministrativeRegionResponse;
import vn.mk.eid.web.service.AdministrativeRegionService;
import vn.mk.eid.web.specification.AdminRegionSpecification;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdministrativeRegionServiceImpl implements AdministrativeRegionService {

    private final AdministrativeRegionRepository administrativeRegionRepository;

    @Override
    public ServiceResult getAll(QueryAdministrativeRegionRequest request) {
        List<AdministrativeRegionEntity> regions = administrativeRegionRepository
                .findAll(AdminRegionSpecification.filterByKeyword(request));
        List<AdministrativeRegionResponse> responses = BeanMapper.listCopy(regions, AdministrativeRegionResponse.class);
        return ServiceResult.ok(responses);
    }
}
