package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DailyStatisticsFactEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatisticsFactRepository extends JpaRepository<DailyStatisticsFactEntity,Long> {
    Optional<DailyStatisticsFactEntity> findByReportDate(LocalDate reportDate);
    @Query("SELECT d FROM DailyStatisticsFactEntity d ORDER BY d.reportDate DESC")
    List<DailyStatisticsFactEntity> findAllOrderByDateDesc();

    @Query("SELECT d FROM DailyStatisticsFactEntity d " +
            "WHERE d.reportDate BETWEEN :startDate AND :endDate ORDER BY d.reportDate")
    List<DailyStatisticsFactEntity> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}