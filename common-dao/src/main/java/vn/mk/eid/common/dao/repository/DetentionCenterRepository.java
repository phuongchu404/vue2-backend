package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetentionCenterRepository extends JpaRepository<DetentionCenterEntity,Integer>, JpaSpecificationExecutor<DetentionCenterEntity> {
    Optional<DetentionCenterEntity> findByCode(String code);

    Page<DetentionCenterEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<DetentionCenterEntity> findByIsActiveTrue();

    List<DetentionCenterEntity> findByProvinceId(String provinceId);

//    @Query("SELECT d FROM DetentionCenterEntity d WHERE " +
//            "(:detentionCenterCode IS NULL OR d.code = :detentionCenterCode) AND " +
//            "(:detentionCenterName IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :detentionCenterName, '%'))) AND " +
//            "(:status IS NULL OR d.isActive = CASE WHEN :status = 'active' THEN true WHEN :status = 'inactive' THEN false END)")
@Query("SELECT d FROM DetentionCenterEntity d WHERE " +
        "(:detentionCenterCode IS NULL OR d.code = :detentionCenterCode) AND " +
        "(:detentionCenterName IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:detentionCenterName AS string), '%'))) AND " +
        "(:status IS NULL OR d.isActive = :status)")
Page<DetentionCenterEntity> searchDetentionCenters(@Param("detentionCenterCode") String detentionCenterCode, @Param("detentionCenterName") String detentionCenterName,
                                                   @Param("status") Boolean status, Pageable pageable);
}
