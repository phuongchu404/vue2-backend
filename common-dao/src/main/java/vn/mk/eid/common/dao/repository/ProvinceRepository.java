package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.ProvinceEntity;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity,String>, JpaSpecificationExecutor<ProvinceEntity> {
    Optional<ProvinceEntity> findByCode(String code);

    Optional<ProvinceEntity> findByName(String name);
}
