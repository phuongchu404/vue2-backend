package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.WardEntity;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<WardEntity, String>, JpaSpecificationExecutor<WardEntity> {
    Optional<WardEntity> findByCode(String code);

    Optional<WardEntity> findByName(String name);

    Optional<WardEntity> findByProvinceCode(String provinceCode);
}