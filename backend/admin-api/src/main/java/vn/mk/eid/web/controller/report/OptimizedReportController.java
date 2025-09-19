package vn.mk.eid.web.controller.report;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.report.OverviewStatistics;
import vn.mk.eid.web.dto.report.ReportResponse;
import vn.mk.eid.web.service.ETLService;
import vn.mk.eid.web.service.OptimizedReportService;
import vn.mk.eid.web.service.ReportCacheService;

import java.time.Duration;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class OptimizedReportController {
    private final OptimizedReportService reportService;
    private final ETLService etlService;
    private final ReportCacheService cacheService;

    /**
     * Thống kê tổng quan (cached)
     */
    @GetMapping("/statistics/overview")
    @Cacheable(value = "overview-stats", key = "'current'")
    public ResponseEntity<OverviewStatistics> getOverviewStatistics() {
        OverviewStatistics stats = reportService.getOverviewStatistics();
//        return ServiceResult.ok(stats);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(15)))
                .body(stats);
    }

    /**
     * Force refresh thống kê tổng quan
     */
    @PostMapping("/statistics/overview/refresh")
    public ResponseEntity<OverviewStatistics> refreshOverviewStatistics() {
        cacheService.clearAllReportCache();
        etlService.calculateDailyStatistics(LocalDate.now());
        OverviewStatistics stats = reportService.getOverviewStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Báo cáo phạm nhân theo trạng thái (cached)
     */
    @GetMapping("/reports/detainees-by-status")
    @Cacheable(value = "detainee-status", key = "#fromDate + '_' + #toDate")
    public ResponseEntity<ReportResponse> getDetaineesByStatus(
            @RequestParam(required = false)  LocalDate fromDate,
            @RequestParam(required = false)  LocalDate toDate) {

        ReportResponse report = reportService.getDetaineesByStatusReport(fromDate, toDate);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30)))
                .body(report);
    }

    /**
     * Báo cáo xu hướng theo tháng
     */
    @GetMapping("/reports/monthly-trends")
    @Cacheable(value = "monthly-reports", key = "#months")
    public ResponseEntity<ReportResponse> getMonthlyTrends(
            @RequestParam(defaultValue = "6") int months) {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        ReportResponse report = reportService.getDetaineesByMonthReport(startDate, endDate);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)))
                .body(report);
    }

    /**
     * API để trigger ETL manually
     */
    @PostMapping("/admin/etl/daily/{date}")
    public ResponseEntity<String> triggerDailyETL(
            @PathVariable LocalDate date) {

        try {
            etlService.calculateDailyStatistics(date);
            cacheService.clearAllReportCache();
            return ResponseEntity.ok("Daily ETL completed for date: " + date);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ETL failed: " + e.getMessage());
        }
    }

    /**
     * API để trigger ETL monthly
     */
    @PostMapping("/admin/etl/monthly/{year}/{month}")
    public ResponseEntity<String> triggerMonthlyETL(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        try {
            etlService.calculateMonthlyStatistics(year, month);
            cacheService.clearAllReportCache();
            return ResponseEntity.ok(String.format("Monthly ETL completed for %d/%d", year, month));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("ETL failed: " + e.getMessage());
        }
    }
}
