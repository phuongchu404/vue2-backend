package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.MonthlyStatisticsFactEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyStatisticsFactRepository extends JpaRepository<MonthlyStatisticsFactEntity, Long> {
    Optional<MonthlyStatisticsFactEntity> findByYearAndMonth(Integer year, Integer month);

    @Query("SELECT m FROM MonthlyStatisticsFactEntity m ORDER BY m.year DESC, m.month DESC")
    List<MonthlyStatisticsFactEntity> findAllOrderByYearMonthDesc();

    @Query("SELECT m FROM MonthlyStatisticsFactEntity m WHERE " +
            "(m.year > :startYear OR (m.year = :startYear AND m.month >= :startMonth)) AND " +
            "(m.year < :endYear OR (m.year = :endYear AND m.month <= :endMonth)) " +
            "ORDER BY m.year, m.month")
    List<MonthlyStatisticsFactEntity> findByYearMonthRange(
            @Param("startYear") Integer startYear, @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear, @Param("endMonth") Integer endMonth
    );
}
