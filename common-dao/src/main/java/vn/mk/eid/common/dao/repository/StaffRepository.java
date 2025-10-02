package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.StaffEntity;

import java.time.LocalDate;
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

    @Query(value = "select count(id) " +
            " from staff " +
            " where is_active = true and (?1 is null OR created_at < DATE_TRUNC('month', CURRENT_DATE))", nativeQuery = true)
    Optional<Integer> getTotalStaff(Boolean isPreviousMonth);

    @Query(value = "select s from StaffEntity s where s.isActive = true order by s.updatedAt desc")
    List<StaffEntity> findTop3ByOrderByUpdateAtDesc(Pageable pageable);

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE s.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE s.isActive = true")
    Long countActiveStaff();

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE s.isActive = true AND s.departmentId = :departmentId AND DATE(s.createdAt) BETWEEN :startDate AND :endDate")
    Long countActiveStaffInPeriodByDepartmentId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("departmentId") Integer departmentId);

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE s.isActive = true AND DATE(s.createdAt) BETWEEN :startDate AND :endDate")
    Long countActiveStaffInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE DATE(s.createdAt) BETWEEN :startDate AND :endDate")
    Long countStaffInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE DATE(s.createdAt) BETWEEN :startDate AND :endDate AND s.departmentId = :departmentId")
    Long countStaffInPeriodByDepartmentId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("departmentId") Integer departmentId);

    @Query("SELECT d.name, COUNT(s), SUM(CASE WHEN (s.status = 'ACTIVE' and s.isActive = true) THEN 1 ELSE 0 END) " +
            "FROM StaffEntity s JOIN DepartmentEntity d ON s.departmentId = d.id " +
            "GROUP BY d.id, d.name")
    List<Object[]> getStaffByDepartmentStatistics();

    @Query("SELECT COUNT(s) FROM StaffEntity s WHERE s.isActive = false AND DATE(s.createdAt) BETWEEN :startDate AND :endDate")
    Long countTerminatedStaffInPeriod(LocalDate startDate, LocalDate endDate);
}
