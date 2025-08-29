package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.EthnicityEntity;

import java.util.Optional;

@Repository
public interface EthnicityRepository extends JpaRepository<EthnicityEntity,Integer>, JpaSpecificationExecutor<EthnicityEntity> {

    Optional<EthnicityEntity> findByCode(String code);

    Optional<EthnicityEntity> findByName(String name);
}
