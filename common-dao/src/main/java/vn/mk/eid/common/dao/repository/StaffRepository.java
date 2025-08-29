package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.StaffEntity;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Integer>, JpaSpecificationExecutor<StaffEntity> {
    Optional<StaffEntity> findByStaffCode(String staffCode);

    Optional<StaffEntity> findByIdNumber(String idNumber);
    Optional<StaffEntity> findByIdNumberAndIdNot(String idNumber, Integer id);

    Page<StaffEntity> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    @Query(value = "select s from StaffEntity s " +
            "left join DetentionCenterEntity d on s.detentionCenterId = d.id " +
            "where d.id = :detentionCenterId")
    Page<StaffEntity> findByDetentionCenterId(Integer detentionCenterId, Pageable pageable);

    @Query(value = "select s from StaffEntity s " +
            "left join DepartmentEntity d on s.departmentId = d.id " +
            "where d.id = :departmentId")
    Page<StaffEntity> findByDepartmentId(Integer departmentId, Pageable pageable);

    @Query(value = "select s from StaffEntity s " +
            "left join PositionEntity p on s.positionId = p.id " +
            "where p.id = :positionId")
    Page<StaffEntity> findByPositionId(Integer positionId, Pageable pageable);

    Page<StaffEntity> findByStatus(String status, Pageable pageable);

    List<StaffEntity> findByIsActiveTrue();

    Long countByDetentionCenterIdAndIsActive(Integer detentionCenterId, Boolean isActive);
}
