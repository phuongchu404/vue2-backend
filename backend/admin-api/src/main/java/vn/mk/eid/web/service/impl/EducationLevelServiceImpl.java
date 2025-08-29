package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.EducationLevelEntity;
import vn.mk.eid.common.dao.entity.PositionEntity;
import vn.mk.eid.common.dao.repository.EducationLevelRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryEducationLevelRequest;
import vn.mk.eid.web.service.EducationLevelService;
import vn.mk.eid.web.specification.EducationLevelSpecification;
import vn.mk.eid.web.specification.PositionSpecification;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EducationLevelServiceImpl implements EducationLevelService {
    private final EducationLevelRepository educationLevelRepository;

    @Override
    public ServiceResult getWithPaging(QueryEducationLevelRequest request) {
        Pageable pageable = null;
        if (request.getPageNo() != null && request.getPageSize() != null) {
            pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        }
        List<EducationLevelEntity> educationLevels;
        if (pageable == null) {
            educationLevels = educationLevelRepository.findAll(EducationLevelSpecification.getEducationLevelSpecification(request));
        } else {
            educationLevels = educationLevelRepository.findAll(EducationLevelSpecification.getEducationLevelSpecification(request), pageable).getContent();
        }
        return ServiceResult.ok(educationLevels);
    }
}
