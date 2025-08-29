package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.ReligionEntity;

import java.util.Optional;

@Repository
public interface ReligionRepository extends JpaRepository<ReligionEntity,Integer>, JpaSpecificationExecutor<ReligionEntity> {

    Optional<ReligionEntity> findByCode(String code);

    Optional<ReligionEntity> findByName(String name);
}
