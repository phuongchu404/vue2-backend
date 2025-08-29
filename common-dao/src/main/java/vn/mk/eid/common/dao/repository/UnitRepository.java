package vn.mk.eid.common.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.UnitEntity;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Long>, JpaSpecificationExecutor<UnitEntity> {
}