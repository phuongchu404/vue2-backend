package vn.mk.eid.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DailyStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.entity.DepartmentStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.MonthlyStatisticsFactEntity;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.web.exception.ETLException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ETLService {
    
    private final DetaineeRepository detaineeRepository;

    
    private final StaffRepository staffRepository;

    
    private final IdentityRecordRepository identityRecordRepository;

    
    private final FingerprintCardRepository fingerprintCardRepository;

    
    private final DailyStatisticsFactRepository dailyStatsRepository;

    
    private final MonthlyStatisticsFactRepository monthlyStatsRepository;

    
    private final DepartmentStatisticsFactRepository departmentStatsRepository;

    
    private final DepartmentRepository departmentRepository;



    /**
     * Tính toán thống kê department hàng ngày - chạy sau daily statistics
     */
    @Scheduled(cron = "0 45 23 * * ?")
    public void calculateDepartmentStatistics() {
        calculateDepartmentStatistics(LocalDate.now());
    }

    /**
     * Tính toán thống kê hàng ngày - chạy tự động lúc 23:30 mỗi ngày
     */
    @Scheduled(cron = "0 30 23 * * ?")
    public void calculateDailyStatistics() {
        calculateDailyStatistics(LocalDate.now());
    }

    /**
     * Tính toán thống kê cho một ngày cụ thể
     */
    public void calculateDailyStatistics(LocalDate targetDate) {
        try {
            log.info("Calculating daily statistics for date: {}", targetDate);

            // Xóa dữ liệu cũ nếu có
            dailyStatsRepository.findByReportDate(targetDate)
                    .ifPresent(dailyStatsRepository::delete);

            // Tính toán các metrics
            DailyStatisticsFactEntity stats = new DailyStatisticsFactEntity();
            stats.setReportDate(targetDate);

            // Detainee metrics
            stats.setTotalDetainees(getTotalDetaineesUpToDate(targetDate));
            stats.setNewDetainees(getNewDetaineesOnDate(targetDate));
            stats.setReleasedDetainees(getReleasedDetaineesOnDate(targetDate));
            stats.setActiveDetainees(getActiveDetaineesUpToDate(targetDate));

            // Staff metrics
            stats.setTotalStaff(getTotalStaffUpToDate(targetDate));
            stats.setNewStaff(getNewStaffOnDate(targetDate));
            stats.setActiveStaff(getActiveStaffUpToDate(targetDate));

            // Identity records metrics
            stats.setTotalIdentityRecords(getTotalIdentityRecordsUpToDate(targetDate));
            stats.setNewIdentityRecords(getNewIdentityRecordsOnDate(targetDate));

            // Fingerprint cards metrics
            stats.setTotalFingerprintCards(getTotalFingerprintCardsUpToDate(targetDate));
            stats.setNewFingerprintCards(getNewFingerprintCardsOnDate(targetDate));

            dailyStatsRepository.save(stats);

            log.info("Daily statistics calculated successfully for date: {}", targetDate);

        } catch (Exception e) {
            log.error("Error calculating daily statistics for date: {}", targetDate, e);
            throw new RuntimeException("Failed to calculate daily statistics", e);
        }
    }

    /**
     * Tính toán thống kê hàng tháng - chạy tự động ngày 1 hàng tháng lúc 1:00
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void calculateMonthlyStatistics() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        calculateMonthlyStatistics(lastMonth.getYear(), lastMonth.getMonthValue());
    }

    /**
     * Tính toán thống kê cho một tháng cụ thể
     */
    public void calculateMonthlyStatistics(Integer year, Integer month) {
        try {
            log.info("Calculating monthly statistics for {}/{}", year, month);

            // Xóa dữ liệu cũ nếu có
            monthlyStatsRepository.findByYearAndMonth(year, month)
                    .ifPresent(monthlyStatsRepository::delete);

            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

            MonthlyStatisticsFactEntity stats = new MonthlyStatisticsFactEntity();
            stats.setYear(year);
            stats.setMonth(month);

            // Aggregate từ daily statistics nếu có, nếu không thì tính trực tiếp
            List<DailyStatisticsFactEntity> dailyStats = dailyStatsRepository
                    .findByDateRange(startOfMonth, endOfMonth);

            if (!dailyStats.isEmpty()) {
                // Aggregate từ daily statistics
                stats.setNewDetainees(dailyStats.stream()
                        .mapToLong(DailyStatisticsFactEntity::getNewDetainees).sum());
                stats.setNewStaff(dailyStats.stream()
                        .mapToLong(DailyStatisticsFactEntity::getNewStaff).sum());
                stats.setNewIdentityRecords(dailyStats.stream()
                        .mapToLong(DailyStatisticsFactEntity::getNewIdentityRecords).sum());
                stats.setNewFingerprintCards(dailyStats.stream()
                        .mapToLong(DailyStatisticsFactEntity::getNewFingerprintCards).sum());

                // Lấy total từ ngày cuối tháng
                DailyStatisticsFactEntity lastDayStats = dailyStats.get(dailyStats.size() - 1);
                stats.setTotalDetainees(lastDayStats.getTotalDetainees());
                stats.setTotalStaff(lastDayStats.getTotalStaff());
            } else {
                // Tính trực tiếp từ raw data
                stats.setNewDetainees(getNewDetaineesInMonth(year, month));
                stats.setReleasedDetainees(getReleasedDetaineesInMonth(year, month));
                stats.setNewStaff(getNewStaffInMonth(year, month));
                stats.setNewIdentityRecords(getNewIdentityRecordsInMonth(year, month));
                stats.setNewFingerprintCards(getNewFingerprintCardsInMonth(year, month));

                stats.setTotalDetainees(getTotalDetaineesUpToDate(endOfMonth));
                stats.setTotalStaff(getTotalStaffUpToDate(endOfMonth));
            }

            monthlyStatsRepository.save(stats);

            log.info("Monthly statistics calculated successfully for {}/{}", year, month);

        } catch (Exception e) {
            log.error("Error calculating monthly statistics for {}/{}", year, month, e);
            throw new RuntimeException("Failed to calculate monthly statistics", e);
        }
    }

    // Helper methods for calculations
    private Long getTotalDetaineesUpToDate(LocalDate date) {
        return detaineeRepository.countDetaineesInPeriod(LocalDate.of(1900, 1, 1), date);
    }

    private Long getNewDetaineesOnDate(LocalDate date) {
        return detaineeRepository.countDetaineesInPeriod(date, date);
    }

    private Long getReleasedDetaineesOnDate(LocalDate date) {

        return detaineeRepository.countReleasedDetaineeInPeriod(date, date);
    }

    private Long getActiveDetaineesUpToDate(LocalDate date) {

        return detaineeRepository.countDetainedInPeriod(LocalDate.of(1900, 1, 1), date);
    }

    private Long getTotalStaffUpToDate(LocalDate date) {
        return staffRepository.countStaffInPeriod(LocalDate.of(1900, 1, 1), date);
    }

    private Long getNewStaffOnDate(LocalDate date) {

        return staffRepository.countStaffInPeriod(date, date);
    }

    private Long getActiveStaffUpToDate(LocalDate date) {

        return staffRepository.countActiveStaffInPeriod(LocalDate.of(1900, 1, 1),date);
    }

    private Long getTotalIdentityRecordsUpToDate(LocalDate date) {
        return identityRecordRepository.countIdentityInPeriod(LocalDate.of(1900, 1, 1), date);
    }

    private Long getNewIdentityRecordsOnDate(LocalDate date) {
        return identityRecordRepository.countIdentityInPeriod(date, date);
    }

    private Long getTotalFingerprintCardsUpToDate(LocalDate date) {
        return fingerprintCardRepository.countFingerprintInPeriod(LocalDate.of(1900, 1, 1), date);
    }

    private Long getNewFingerprintCardsOnDate(LocalDate date) {
        return fingerprintCardRepository.countFingerprintInPeriod(date, date);
    }

    private Long getNewDetaineesInMonth(Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return detaineeRepository.countDetaineesInPeriod(startOfMonth, endOfMonth);
    }

    private Long getReleasedDetaineesInMonth(Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return detaineeRepository.countReleasedDetaineeInPeriod(startOfMonth, endOfMonth);
    }


    private Long getNewStaffInMonth(Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return staffRepository.countStaffInPeriod(startOfMonth, endOfMonth);
    }

    private Long getNewIdentityRecordsInMonth(Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        return identityRecordRepository.countIdentityInPeriod(startOfMonth, endOfMonth);
    }

    private Long getNewFingerprintCardsInMonth(Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        return fingerprintCardRepository.countFingerprintInPeriod(startOfMonth, endOfMonth);
    }

    /**
     * Tính toán thống kê cho department theo ngày
     */
    public void calculateDepartmentStatistics(LocalDate targetDate) {
        try {
            log.info("Calculating department statistics for date: {}", targetDate);

            List<DepartmentEntity> activeDepartments = departmentRepository.findAllActive();

            for (DepartmentEntity dept : activeDepartments) {
                // Xóa dữ liệu cũ nếu có
                departmentStatsRepository.findByDepartmentIdAndReportDate(dept.getId(), targetDate)
                        .ifPresent(departmentStatsRepository::delete);

                // Tính toán metrics cho department
                DepartmentStatisticsFactEntity deptStats = new DepartmentStatisticsFactEntity();
                deptStats.setDepartmentId(dept.getId());
                deptStats.setReportDate(targetDate);

                // Đếm staff trong department
                Long staffCount = getStaffCountByDepartmentAndDate(dept.getId(), targetDate);
                Long activeStaffCount = getActiveStaffCountByDepartmentAndDate(dept.getId(), targetDate);
//                Integer detaineesAssigned = getDetaineesAssignedToDepartment(dept.getId(), targetDate);

                deptStats.setStaffCount(staffCount);
                deptStats.setActiveStaffCount(activeStaffCount);
//                deptStats.setDetaineesAssigned(detaineesAssigned);

                departmentStatsRepository.save(deptStats);
            }

            log.info("Department statistics calculated successfully for date: {}", targetDate);

        } catch (Exception e) {
            log.error("Error calculating department statistics for date: {}", targetDate, e);
            throw new ETLException("Failed to calculate department statistics", e);
        }
    }

    // Helper methods for department statistics
    private Long getStaffCountByDepartmentAndDate(Integer departmentId, LocalDate date) {
        // Count all staff ever assigned to department up to this date
        return staffRepository.countStaffInPeriodByDepartmentId(LocalDate.of(1900, 1, 1), date, departmentId);
    }

    private Long getActiveStaffCountByDepartmentAndDate(Integer departmentId, LocalDate date) {
        // Count active staff in department on this date
        return staffRepository.countActiveStaffInPeriodByDepartmentId(date, date, departmentId);
    }

//    private Integer getDetaineesAssignedToDepartment(Integer departmentId, LocalDate date) {
//        // Count detainees assigned to this department
//        return detaineeRepository.countDetainedDetainees();
//    }
}
