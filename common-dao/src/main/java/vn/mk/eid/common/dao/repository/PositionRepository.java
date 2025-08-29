package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.PositionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity,Integer>, JpaSpecificationExecutor<PositionEntity> {
    Optional<PositionEntity> findByCode(String code);

    Optional<PositionEntity> findByName(String name);

    List<PositionEntity> findByAppliesTo(String appliesTo);

    List<PositionEntity> findByAppliesToIn(List<String> appliesToList);


}
