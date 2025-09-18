package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DepartmentStatisticsFactEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentStatisticsFactRepository extends JpaRepository<DepartmentStatisticsFactEntity, Long> {
    @Query("SELECT d FROM DepartmentStatisticsFactEntity d WHERE d.reportDate = :reportDate ORDER BY d.departmentId")
    List<DepartmentStatisticsFactEntity> findByReportDate(@Param("reportDate") LocalDate reportDate);

    @Query("SELECT d.departmentId, dept.name, d.activeStaffCount, d.detaineesAssigned, " +
            "CASE WHEN d.activeStaffCount > 0 THEN CAST(d.detaineesAssigned AS DOUBLE) / d.activeStaffCount ELSE 0 END as efficiency " +
            "FROM DepartmentStatisticsFactEntity d JOIN DepartmentEntity dept ON d.departmentId = dept.id " +
            "WHERE d.reportDate = :reportDate ORDER BY d.activeStaffCount DESC")
    List<Object[]> getDepartmentSummaryByDate(@Param("reportDate") LocalDate reportDate);

    @Query("SELECT d FROM DepartmentStatisticsFactEntity d WHERE d.departmentId = :departmentId " +
            "AND d.reportDate BETWEEN :startDate AND :endDate ORDER BY d.reportDate")
    List<DepartmentStatisticsFactEntity> findByDepartmentIdAndDateRange(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Top performing departments by efficiency
    @Query(value = "SELECT d.department_id, dept.name, " +
            "AVG(CASE WHEN d.active_staff_count > 0 THEN d.detainees_assigned / d.active_staff_count ELSE 0 END) as avg_efficiency " +
            "FROM department_statistics_fact d " +
            "JOIN departments dept ON d.department_id = dept.id " +
            "WHERE d.report_date BETWEEN :startDate AND :endDate " +
            "GROUP BY d.department_id, dept.name " +
            "ORDER BY avg_efficiency DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopPerformingDepartments(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("limit") int limit);

    // Department workload distribution
    @Query("SELECT d.departmentId, dept.name, " +
            "SUM(d.detaineesAssigned) as totalDetainees, " +
            "AVG(d.activeStaffCount) as avgStaff " +
            "FROM DepartmentStatisticsFactEntity d JOIN DepartmentEntity dept ON d.departmentId = dept.id " +
            "WHERE d.reportDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.departmentId, dept.name " +
            "ORDER BY totalDetainees DESC")
    List<Object[]> getDepartmentWorkloadDistribution(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    Optional<DepartmentStatisticsFactEntity> findByDepartmentIdAndReportDate(Integer id, LocalDate targetDate);
}
