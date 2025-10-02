package vn.mk.eid.web.controller.report;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.web.dto.report.OverviewStatistics;
import vn.mk.eid.web.dto.report.ReportResponse;
import vn.mk.eid.web.service.ETLService;
import vn.mk.eid.web.service.OptimizedReportService;

import java.time.Duration;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class OptimizedReportController {
    private final OptimizedReportService reportService;
    private final ETLService etlService;
//    private final ReportCacheService cacheService;

    /**
     * Thống kê tổng quan (cached)
     */
    @GetMapping("/statistics/overview")
    public ResponseEntity<OverviewStatistics> getOverviewStatistics() {
        OverviewStatistics stats = reportService.getOverviewStatistics();
//        return ServiceResult.ok(stats);
        reportService.clearAllReportCache();
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(15)))
                .body(stats);
    }

    /**
     * Force refresh thống kê tổng quan
     */
    @PostMapping("/statistics/overview/refresh")
    public ResponseEntity<OverviewStatistics> refreshOverviewStatistics() {
        reportService.clearAllReportCache();
        etlService.calculateDailyStatistics(LocalDate.now());
        OverviewStatistics stats = reportService.getOverviewStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Báo cáo phạm nhân theo trạng thái (cached)
     */
    @GetMapping("/detainees-by-status")
    public ResponseEntity<ReportResponse> getDetaineesByStatus(
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate fromDate,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate toDate) {

        reportService.clearAllReportCache();
        ReportResponse report = reportService.getDetaineesByStatusReport(fromDate, toDate);
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30)))
                .body(report);
    }

    /**
     * Báo cáo xu hướng theo tháng
     */
    @GetMapping("/monthly-trends")
    public ResponseEntity<ReportResponse> getMonthlyTrends(@RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate fromDate,
                                                           @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate toDate) {

//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusMonths(months);
        reportService.clearAllReportCache();
        ReportResponse report = reportService.getDetaineesByMonthReport(fromDate, toDate);
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)))
                .body(report);
    }

    /**
     * API để trigger ETL manually
     */
    @PostMapping("/etl/daily/{date}")
    public ResponseEntity<String> triggerDailyETL(
            @PathVariable LocalDate date) {

        try {
            etlService.calculateDailyStatistics(date);
            reportService.clearAllReportCache();
            return ResponseEntity.ok("Daily ETL completed for date: " + date);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ETL failed: " + e.getMessage());
        }
    }

    /**
     * API để trigger ETL monthly
     */
    @PostMapping("/etl/monthly/{year}/{month}")
    public ResponseEntity<String> triggerMonthlyETL(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        try {
            etlService.calculateMonthlyStatistics(year, month);
            reportService.clearAllReportCache();
            return ResponseEntity.ok(String.format("Monthly ETL completed for %d/%d", year, month));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ETL failed: " + e.getMessage());
        }
    }

    /**
     * Báo cáo cán bộ theo phòng ban (enhanced with department stats)
     */
    @GetMapping("/staff-by-department")
    public ResponseEntity<ReportResponse> getStaffByDepartment() {
        reportService.clearAllReportCache();
        ReportResponse report = reportService.getStaffByDepartmentReport();
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30)))
                .body(report);
    }

    /**
     * Báo cáo danh bản đã lập
     */
    @GetMapping("/identity-records")
    public ResponseEntity<ReportResponse> getIdentityRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        // Set default dates if not provided
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (fromDate == null) {
            fromDate = toDate.minusMonths(1); // Default to last month
        }
        reportService.clearAllReportCache();
        ReportResponse report = reportService.getIdentityRecordsReport(fromDate, toDate);
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(20)))
                .body(report);
    }

    /**
     * Báo cáo chỉ bản đã lập
     */
    @GetMapping("/fingerprint-cards")
    public ResponseEntity<ReportResponse> getFingerprintCards(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        // Set default dates if not provided
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        if (fromDate == null) {
            fromDate = toDate.minusMonths(1); // Default to last month
        }
        reportService.clearAllReportCache();
        ReportResponse report = reportService.getFingerprintCardsReport(fromDate, toDate);
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(20)))
                .body(report);
    }

    /**
     * Báo cáo tổng hợp theo tháng
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<ReportResponse> getMonthlySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {

        // Default to current month if not provided
        if (month == null) {
            month = LocalDate.now().withDayOfMonth(1);
        }

        ReportResponse report = reportService.getMonthlySummaryReport(month);
        return ResponseEntity.ok()
//                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)))
                .body(report);
    }

//    /**
//     * Báo cáo tổng hợp theo khoảng thời gian tùy chỉnh
//     */
//    @GetMapping("/reports/period-summary")
//    @Cacheable(value = "period-summary", key = "#fromDate + '_' + #toDate")
//    public ResponseEntity<ReportResponse> getPeriodSummary(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
//
//        // Validate date range
//        if (fromDate.isAfter(toDate)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Limit to max 1 year range
//        if (fromDate.until(toDate).getDays() > 365) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        try {
//            ReportResponse report = generatePeriodSummary(fromDate, toDate);
//            return ResponseEntity.ok()
//                    .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30)))
//                    .body(report);
//        } catch (Exception e) {
//            log.error("Error generating period summary report", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }


}
