package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.EducationLevelEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationLevelRepository extends JpaRepository<EducationLevelEntity,Integer>, JpaSpecificationExecutor<EducationLevelEntity> {

    Optional<EducationLevelEntity> findByCode(String code);

    List<EducationLevelEntity> findAllByOrderByLevelOrderAsc();
}
