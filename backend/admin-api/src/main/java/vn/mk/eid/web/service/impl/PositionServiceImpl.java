package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.entity.PositionEntity;
import vn.mk.eid.common.dao.repository.PositionRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryPositionRequest;
import vn.mk.eid.web.service.PositionService;
import vn.mk.eid.web.specification.DepartmentSpecification;
import vn.mk.eid.web.specification.PositionSpecification;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    @Override
    public ServiceResult getWithPaging(QueryPositionRequest request) {
        Pageable pageable = null;
        if (request.getPageNo() != null && request.getPageSize() != null) {
            pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        }
        List<PositionEntity> positions;
        if (pageable == null) {
            positions = positionRepository.findAll(PositionSpecification.getPositionSpecification(request));
        } else {
            positions = positionRepository.findAll(PositionSpecification.getPositionSpecification(request), pageable).getContent();
        }
        return ServiceResult.ok(positions);
    }
}
