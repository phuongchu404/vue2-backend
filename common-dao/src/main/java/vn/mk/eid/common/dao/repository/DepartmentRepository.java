package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DepartmentEntity;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer>, JpaSpecificationExecutor<DepartmentEntity> {
    @Query(value = "SELECT d FROM DepartmentEntity d " +
            "LEFT JOIN DetentionCenterEntity dt ON d.detentionCenterId = dt.id " +
            "WHERE d.code = :code AND dt.id = :detentionCenterId")
    Optional<DepartmentEntity> findByCodeAndDetentionCenterId(String code, Integer detentionCenterId);

    @Query(value = "SELECT d FROM DepartmentEntity d " +
            "LEFT JOIN DetentionCenterEntity dt ON d.detentionCenterId = dt.id " +
            "WHERE dt.id = :detentionCenterId")
    List<DepartmentEntity> findByDetentionCenterId(@Param("detentionCenterId") Integer detentionCenterId);

    @Query(value = "SELECT d FROM DepartmentEntity d " +
            "LEFT JOIN DetentionCenterEntity dt ON d.detentionCenterId = dt.id " +
            "WHERE dt.id = :detentionCenterId AND d.isActive = true")
    List<DepartmentEntity> findByDetentionCenterIdAndIsActiveTrue(@Param("detentionCenterId") Integer detentionCenterId);

    Optional<DepartmentEntity> findByIdAndDetentionCenterId(Integer id, @NotNull Integer detentionCenterId);

    Optional<DepartmentEntity> findByCode(String code);

    @Query("SELECT d FROM DepartmentEntity d WHERE d.isActive=true ORDER BY d.name")
    List<DepartmentEntity> findAllActive();

}
