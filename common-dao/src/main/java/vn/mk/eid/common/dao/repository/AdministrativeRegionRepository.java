package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.AdministrativeRegionEntity;
@Repository
public interface AdministrativeRegionRepository extends JpaRepository<AdministrativeRegionEntity, Integer> {

}
