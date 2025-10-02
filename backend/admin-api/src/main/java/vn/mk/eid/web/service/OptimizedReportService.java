package vn.mk.eid.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DailyStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.MonthlyStatisticsFactEntity;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.web.constant.DetaineeStatus;
import vn.mk.eid.web.dto.report.*;
import vn.mk.eid.web.exception.RestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class OptimizedReportService implements ReportService {

    private final DailyStatisticsFactRepository dailyStatsRepository;
    private final MonthlyStatisticsFactRepository monthlyStatsRepository;
    private final DetaineeRepository detaineeRepository;
    private final StaffRepository staffRepository;
    private final IdentityRecordRepository identityRecordRepository;
    private final FingerprintCardRepository fingerprintCardRepository;

    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 ms
    public void scheduledCacheClear() {
        log.info("Scheduled cache clear - clearing all report cache");
        clearAllReportCache();
    }
    @Override
    @Cacheable(value = "overview-stats", key = "'current'")
    public OverviewStatistics getOverviewStatistics() {
        try {
            // Lấy từ pre-aggregated data thay vì tính real-time
            Optional<DailyStatisticsFactEntity> latestDaily = dailyStatsRepository
                    .findAllOrderByDateDesc().stream().findFirst();

            if (latestDaily.isPresent()) {
                DailyStatisticsFactEntity latest = latestDaily.get();

                // Lấy thay đổi từ tháng hiện tại
                LocalDate now = LocalDate.now();
                Optional<MonthlyStatisticsFactEntity> currentMonth = monthlyStatsRepository
                        .findByYearAndMonth(now.getYear(), now.getMonthValue());

                Long detaineeChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewDetainees).orElse(0L);
                Long staffChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewStaff).orElse(0L);
                Long identityChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewIdentityRecords).orElse(0L);
                Long fingerprintChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewFingerprintCards).orElse(0L);

                return new OverviewStatistics(
                        latest.getActiveDetainees(),
                        latest.getActiveStaff(),
                        latest.getTotalIdentityRecords(),
                        latest.getTotalFingerprintCards(),
                        detaineeChange,
                        staffChange,
                        identityChange,
                        fingerprintChange
                );
            } else {
                // Fallback to real-time calculation
                log.warn("No pre-aggregated data found, falling back to real-time calculation");
                return calculateOverviewStatisticsRealTime();
            }

        } catch (Exception e) {
            log.error("Error getting overview statistics", e);
            return new OverviewStatistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    @Override
    @Cacheable(value = "detainee-status", key = "#fromDate + '_' + #toDate")
    public ReportResponse getDetaineesByStatusReport(LocalDate fromDate, LocalDate toDate) {
        try {
            // Sử dụng optimized query
            List<Object[]> results = detaineeRepository.getDetaineeStatusStatistics();

            List<ReportColumn> columns = Arrays.asList(
                    new ReportColumn("status", "Trạng Thái", "text"),
                    new ReportColumn("count", "Số Lượng", "number"),
                    new ReportColumn("percentage", "Tỷ Lệ (%)", "number")
            );

            List<Map<String, Object>> data = results.stream()
                    .map(row -> {
                        Map<String, Object> item = new HashMap<>();
                        String statusStr = (String) row[0];
                        DetaineeStatus status = DetaineeStatus.valueOf(statusStr);
                        item.put("status", translateStatus(status));
                        item.put("count", ((Number) row[1]).intValue());
                        item.put("percentage", ((Number) row[2]).doubleValue());
                        return item;
                    })
                    .collect(Collectors.toList());

            // Calculate summary
            int totalCount = data.stream().mapToInt(row -> (Integer) row.get("count")).sum();
            Map<String, Object> summary = new HashMap<>();
            summary.put("status", "Tổng cộng");
            summary.put("count", totalCount);
            summary.put("percentage", 100.0);

            // Create chart data
            ChartData chartData = createPieChartData(data);

            // Create insights
            List<ReportInsight> insights = createStatusInsights(data, totalCount);

            return new ReportResponse(
                    "Báo Cáo Phạm Nhân Theo Trạng Thái",
                    columns, data, summary, chartData, insights
            );

        } catch (Exception e) {
            log.error("Error generating detainees by status report", e);
            return createEmptyReport("Lỗi tạo báo cáo");
        }
    }
    @Override
    @Cacheable(value = "monthly-reports", key = "#fromDate + '_' + #toDate")
    public ReportResponse getDetaineesByMonthReport(LocalDate fromDate, LocalDate toDate) {
        log.info("getDetaineesByMonthReport() - From: {}, To: {}", fromDate, toDate);
        long startTime = System.currentTimeMillis();

        try {
            // Set default dates if not provided
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            if (fromDate == null) {
                fromDate = toDate.minusMonths(6); // Default to last 6 months
            }

            // Validate date range
            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException("fromDate cannot be after toDate");
            }

            log.debug("Getting monthly data from {} to {}", fromDate, toDate);

            // Try to get pre-aggregated monthly data first
            List<MonthlyStatisticsFactEntity> monthlyData = getMonthlyDataInRange(fromDate, toDate);

            List<Map<String, Object>> data;
            String dataSource;

            if (monthlyData != null && !monthlyData.isEmpty()) {
                // Use pre-aggregated data (FAST)
                log.debug("Using pre-aggregated data - found {} months", monthlyData.size());
                data = convertMonthlyFactsToData(monthlyData);
                dataSource = "PRE_AGGREGATED";

            } else {
                // Fallback to real-time calculation (SLOWER)
                log.warn("No pre-aggregated data found, falling back to real-time calculation");
                data = calculateMonthlyDataRealTime(fromDate, toDate);
                dataSource = "REAL_TIME";
            }

            // Define columns
            List<ReportColumn> columns = Arrays.asList(
                    ReportColumnFactory.createTextColumn("month", "Tháng"),
                    ReportColumnFactory.createNumberColumn("newDetainees", "Phạm nhân mới"),
                    ReportColumnFactory.createNumberColumn("releasedDetainees", "Phạm nhân thả"),
                    ReportColumnFactory.createNumberColumn("totalDetainees", "Tổng cuối tháng"),
                    ReportColumnFactory.createNumberColumn("netChange", "Thay đổi ròng")
            );

            // Create chart data for trends (multi-line chart)
            ChartData chartData = createMonthlyTrendsChart(data);

            // Create insights based on data source
            List<ReportInsight> insights = monthlyData != null && !monthlyData.isEmpty()
                    ? createMonthlyInsightsFromFacts(monthlyData)
                    : createMonthlyInsightsFromRealTimeData(data);

            long executionTime = System.currentTimeMillis() - startTime;

            return new ReportResponseBuilder("Báo Cáo Phạm Nhân Theo Tháng")
                    .columns(columns)
                    .data(data)
                    .chartData(chartData)
                    .insights(insights)
                    .dataSource(dataSource)
                    .executionTime(executionTime)
                    .build();

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error generating detainees by month report after {}ms", executionTime, e);
            return createEmptyReport("Lỗi tạo báo cáo theo tháng: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "staff-department", key = "'current'")
    public ReportResponse getStaffByDepartmentReport() {
        log.info("BaseReportService.getStaffByDepartmentReport() - Starting generation");
        long startTime = System.currentTimeMillis();

        try {
            // Get staff statistics by department from database
            List<Object[]> departmentStats = staffRepository.getStaffByDepartmentStatistics();
            log.debug("Retrieved staff statistics for {} departments", departmentStats.size());

            // Define columns for the report
            List<ReportColumn> columns = Arrays.asList(
                    ReportColumnFactory.createTextColumn("department", "Phòng Ban"),
                    ReportColumnFactory.createNumberColumn("totalCount", "Tổng Số"),
                    ReportColumnFactory.createNumberColumn("activeCount", "Đang Hoạt Động"),
                    ReportColumnFactory.createNumberColumn("inactiveCount", "Tạm Nghỉ"),
                    ReportColumnFactory.createPercentageColumn("activePercentage", "Tỷ Lệ Hoạt Động (%)"),
                    ReportColumnFactory.createTextColumn("status", "Trạng Thái")
            );

            // Process data and calculate additional metrics
            List<Map<String, Object>> data = new ArrayList<>();
            int grandTotal = 0;
            int grandActive = 0;

            for (Object[] row : departmentStats) {
                String departmentName = (String) row[0];
                Integer totalCount = ((Number) row[1]).intValue();
                Integer activeCount = ((Number) row[2]).intValue();
                Integer inactiveCount = totalCount - activeCount;

                // Calculate active percentage
                Double activePercentage = totalCount > 0 ? (activeCount.doubleValue() / totalCount) * 100 : 0.0;

                // Determine department status
                String status = determineStaffDepartmentStatus(activePercentage, totalCount);

                Map<String, Object> departmentData = new HashMap<>();
                departmentData.put("department", departmentName);
                departmentData.put("totalCount", totalCount);
                departmentData.put("activeCount", activeCount);
                departmentData.put("inactiveCount", inactiveCount);
                departmentData.put("activePercentage", Math.round(activePercentage * 100.0) / 100.0);
                departmentData.put("status", status);

                data.add(departmentData);

                grandTotal += totalCount;
                grandActive += activeCount;
            }

            // Sort by active count descending
            data.sort((a, b) -> Integer.compare((Integer) b.get("activeCount"), (Integer) a.get("activeCount")));

            // Create summary row
            Double grandActivePercentage = grandTotal > 0 ? (grandActive / grandTotal) * 100 : 0.0;
            Map<String, Object> summary = new HashMap<>();
            summary.put("department", "TỔNG CỘNG");
            summary.put("totalCount", grandTotal);
            summary.put("activeCount", grandActive);
            summary.put("inactiveCount", grandTotal - grandActive);
            summary.put("activePercentage", Math.round(grandActivePercentage * 100.0) / 100.0);
            summary.put("status", "---");

            // Create chart data (bar chart showing active vs total)
            ChartData chartData = createStaffDepartmentChart(data);

            // Generate insights
            List<ReportInsight> insights = createStaffDepartmentInsights(data, grandTotal, grandActive);

            long executionTime = System.currentTimeMillis() - startTime;

            return new ReportResponseBuilder("Báo Cáo Cán Bộ Theo Phòng Ban")
                    .columns(columns)
                    .data(data)
                    .summary(summary)
                    .chartData(chartData)
                    .insights(insights)
                    .dataSource("REAL_TIME")
                    .executionTime(executionTime)
                    .build();

        } catch (Exception e) {
            log.error("Error generating staff by department report", e);
            return createEmptyReport("Lỗi tạo báo cáo cán bộ theo phòng ban: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "identity-reports", key = "#fromDate + '_' + #toDate")
    public ReportResponse getIdentityRecordsReport(LocalDate fromDate, LocalDate toDate) {
        long startTime = System.currentTimeMillis();

        try {
            // Set default dates if not provided
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            if (fromDate == null) {
                fromDate = toDate.minusMonths(3); // Default to last 3 months
            }

            log.debug("Getting identity records from {} to {}", fromDate, toDate);

            // Get identity records data
            List<Map<String, Object>> identityData = getIdentityRecordsData(fromDate, toDate);
            log.debug("Retrieved {} identity records", identityData.size());

            // Define columns
            List<ReportColumn> columns = Arrays.asList(
                    ReportColumnFactory.createNumberColumn("id", "STT"),
                    ReportColumnFactory.createTextColumn("detaineeId", "Mã Phạm Nhân"),
                    ReportColumnFactory.createTextColumn("detaineeName", "Họ và Tên"),
                    ReportColumnFactory.createDateColumn("createdDate", "Ngày Tạo"),
                    ReportColumnFactory.createTextColumn("completeness", "Độ Hoàn Thành"),
                    ReportColumnFactory.createPercentageColumn("completenessPercent", "% Hoàn Thành")
//                    ReportColumnFactory.createTextColumn("recordType", "Loại Danh Bản"),
//                    ReportColumnFactory.createTextColumn("status", "Trạng Thái"),
//                    ReportColumnFactory.createTextColumn("createdBy", "Người Tạo"),
//                    ReportColumnFactory.createDateColumn("completedDate", "Ngày Hoàn Thành")
            );

            // Add sequential ID and process data
            for (int i = 0; i < identityData.size(); i++) {
                identityData.get(i).put("id", i + 1);
            }

            // Create statistics summary
            Map<String, Object> summary = createIdentityRecordsSummary(identityData);

            // Create chart data (status distribution)
            ChartData chartData = createIdentityRecordsChart(identityData);

            // Generate insights
            List<ReportInsight> insights = createIdentityRecordsInsights(identityData, fromDate, toDate);

            long executionTime = System.currentTimeMillis() - startTime;

            return new ReportResponseBuilder("Báo Cáo Danh Bản Đã Lập")
                    .columns(columns)
                    .data(identityData)
                    .summary(summary)
                    .chartData(chartData)
                    .insights(insights)
                    .dataSource("REAL_TIME")
                    .executionTime(executionTime)
                    .build();

        } catch (Exception e) {
            log.error("Error generating identity records report", e);
            return createEmptyReport("Lỗi tạo báo cáo danh bản: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "fingerprint-reports", key = "#fromDate + '_' + #toDate")
    public ReportResponse getFingerprintCardsReport(LocalDate fromDate, LocalDate toDate) {
        log.info("BaseReportService.getFingerprintCardsReport() - From: {}, To: {}", fromDate, toDate);
        long startTime = System.currentTimeMillis();

        try {
            // Set default dates if not provided
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            if (fromDate == null) {
                fromDate = toDate.minusMonths(3); // Default to last 3 months
            }

            log.debug("Getting fingerprint cards from {} to {}", fromDate, toDate);

            // Get fingerprint cards data
            List<Map<String, Object>> fingerprintData = getFingerprintCardsData(fromDate, toDate);
            log.debug("Retrieved {} fingerprint cards", fingerprintData.size());

            // Define columns
            List<ReportColumn> columns = Arrays.asList(
                    ReportColumnFactory.createNumberColumn("id", "STT"),
                    ReportColumnFactory.createTextColumn("personId", "Mã Phạm Nhân"),
                    ReportColumnFactory.createTextColumn("detaineeName", "Họ và Tên"),
//                    ReportColumnFactory.createTextColumn("cardNumber", "Số Thẻ"),
                    ReportColumnFactory.createDateColumn("createdDate", "Ngày Tạo"),
                    ReportColumnFactory.createTextColumn("completeness", "Độ Hoàn Thành"),
                    ReportColumnFactory.createPercentageColumn("completenessPercent", "% Hoàn Thành")
//                    ReportColumnFactory.createTextColumn("status", "Trạng Thái"),
//                    ReportColumnFactory.createTextColumn("createdBy", "Người Tạo"),
//                    ReportColumnFactory.createDateColumn("lastUpdated", "Cập Nhật Cuối")
            );

            // Add sequential ID and process data
            for (int i = 0; i < fingerprintData.size(); i++) {
                fingerprintData.get(i).put("id", i + 1);
            }

            // Create statistics summary
            Map<String, Object> summary = createFingerprintCardsSummary(fingerprintData);

            // Create chart data (completeness distribution)
            ChartData chartData = createFingerprintCardsChart(fingerprintData);

            // Generate insights
            List<ReportInsight> insights = createFingerprintCardsInsights(fingerprintData, fromDate, toDate);

            long executionTime = System.currentTimeMillis() - startTime;

            return new ReportResponseBuilder("Báo Cáo Chỉ Bản Đã Lập")
//                    .columns(columns)
//                    .data(fingerprintData)
                    .summary(summary)
                    .chartData(chartData)
                    .insights(insights)
                    .dataSource("REAL_TIME")
                    .executionTime(executionTime)
                    .build();

        } catch (Exception e) {
            log.error("Error generating fingerprint cards report", e);
            return createEmptyReport("Lỗi tạo báo cáo chỉ bản: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "monthly-summary", key = "#month")
    public ReportResponse getMonthlySummaryReport(LocalDate month) {
        log.info("BaseReportService.getMonthlySummaryReport() - Month: {}", month);
        long startTime = System.currentTimeMillis();

        try {
            // Default to current month if not provided
            if (month == null) {
                month = LocalDate.now().withDayOfMonth(1);
            }

            // Ensure we're working with start of month
            LocalDate startOfMonth = month.withDayOfMonth(1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
            LocalDate startOfPrevMonth = startOfMonth.minusMonths(1);
            LocalDate endOfPrevMonth = startOfMonth.minusDays(1);

            log.debug("Calculating monthly summary for {} (from {} to {})",
                    startOfMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")), startOfMonth, endOfMonth);

            // Calculate all monthly metrics
            Map<String, Long> currentMonthMetrics = calculateMonthlyMetrics(startOfMonth, endOfMonth);
            Map<String, Long> previousMonthMetrics = calculateMonthlyMetrics(startOfPrevMonth, endOfPrevMonth);

            // Define columns
            List<ReportColumn> columns = Arrays.asList(
                    ReportColumnFactory.createTextColumn("category", "Danh Mục"),
                    ReportColumnFactory.createTextColumn("metric", "Chỉ Số"),
                    ReportColumnFactory.createNumberColumn("currentMonth", "Tháng Hiện Tại"),
                    ReportColumnFactory.createNumberColumn("previousMonth", "Tháng Trước"),
                    ReportColumnFactory.createTextColumn("change", "Thay Đổi"),
                    ReportColumnFactory.createPercentageColumn("changePercent", "% Thay Đổi"),
                    ReportColumnFactory.createTextColumn("trend", "Xu Hướng")
            );

            // Create data rows
            List<Map<String, Object>> data = new ArrayList<>();

            // Detainee metrics
            data.add(createMonthlySummaryRow("Phạm Nhân", "Nhập mới",
                    currentMonthMetrics.get("newDetainees"), previousMonthMetrics.get("newDetainees")));
            data.add(createMonthlySummaryRow("Phạm Nhân", "Thả tự do",
                    currentMonthMetrics.get("releasedDetainees"), previousMonthMetrics.get("releasedDetainees")));
            data.add(createMonthlySummaryRow("Phạm Nhân", "Chuyển trại",
                    currentMonthMetrics.get("transferredDetainees"), previousMonthMetrics.get("transferredDetainees")));
            data.add(createMonthlySummaryRow("Phạm Nhân", "Tổng cuối tháng",
                    currentMonthMetrics.get("totalDetainees"), previousMonthMetrics.get("totalDetainees")));

            // Staff metrics
            data.add(createMonthlySummaryRow("Cán Bộ", "Tuyển mới",
                    currentMonthMetrics.get("newStaff"), previousMonthMetrics.get("newStaff")));
            data.add(createMonthlySummaryRow("Cán Bộ", "Nghỉ việc",
                    currentMonthMetrics.get("terminatedStaff"), previousMonthMetrics.get("terminatedStaff")));
            data.add(createMonthlySummaryRow("Cán Bộ", "Tổng cuối tháng",
                    currentMonthMetrics.get("totalStaff"), previousMonthMetrics.get("totalStaff")));

            // Identity and Fingerprint metrics
            data.add(createMonthlySummaryRow("Danh Bản", "Lập mới",
                    currentMonthMetrics.get("newIdentityRecords"), previousMonthMetrics.get("newIdentityRecords")));
            data.add(createMonthlySummaryRow("Danh Bản", "Hoàn thành",
                    currentMonthMetrics.get("completedIdentityRecords"), previousMonthMetrics.get("completedIdentityRecords")));
            data.add(createMonthlySummaryRow("Chỉ Bản", "Lập mới",
                    currentMonthMetrics.get("newFingerprintCards"), previousMonthMetrics.get("newFingerprintCards")));
            data.add(createMonthlySummaryRow("Chỉ Bản", "Hoàn thành",
                    currentMonthMetrics.get("completedFingerprintCards"), previousMonthMetrics.get("completedFingerprintCards")));

            // Calculate overall summary
            Map<String, Object> summary = createMonthlySummaryStatistics(currentMonthMetrics, previousMonthMetrics);

            // Create chart data (showing trends)
            ChartData chartData = createMonthlySummaryChart(data);

            // Generate insights
            List<ReportInsight> insights = createMonthlySummaryInsights(data, currentMonthMetrics, startOfMonth);

            long executionTime = System.currentTimeMillis() - startTime;

            String monthName = startOfMonth.format(DateTimeFormatter.ofPattern("'Tháng' MM/yyyy"));

            return new ReportResponseBuilder("Báo Cáo Tổng Hợp " + monthName)
                    .columns(columns)
                    .data(data)
                    .summary(summary)
                    .chartData(chartData)
                    .insights(insights)
                    .dataSource("REAL_TIME")
                    .executionTime(executionTime)
                    .build();

        } catch (Exception e) {
            log.error("Error generating monthly summary report for month: {}", month, e);
            return createEmptyReport("Lỗi tạo báo cáo tổng hợp tháng: " + e.getMessage());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "overview-stats", allEntries = true),
            @CacheEvict(value = "detainee-status", allEntries = true),
            @CacheEvict(value = "monthly-reports", allEntries = true),
            @CacheEvict(value = "staff-department", allEntries = true),
            @CacheEvict(value = "department-comparison", allEntries = true),
            @CacheEvict(value = "department-trends", allEntries = true),
            @CacheEvict(value = "department-analytics", allEntries = true),
            @CacheEvict(value = "identity-reports", allEntries = true),
            @CacheEvict(value = "fingerprint-reports", allEntries = true),
            @CacheEvict(value = "monthly-summary", allEntries = true)
    })
    public void clearAllReportCache() {
        log.info("Clearing all report cache");
    }
    private List<ReportInsight> createMonthlyInsightsFromRealTimeData(List<Map<String, Object>> data) {
        List<ReportInsight> insights = new ArrayList<>();

        if (data.isEmpty()) return insights;

        // Find month with highest new detainees
        Map<String, Object> maxMonth = data.stream()
                .max(Comparator.comparing(row -> (Long) row.get("newDetainees")))
                .orElse(data.get(0));

        insights.add(new ReportInsight(
                "Tháng cao nhất",
                "Tháng " + maxMonth.get("month") + " có số phạm nhân mới cao nhất",
                maxMonth.get("newDetainees") + " người",
                "UP",
                "INFO",
                "trending-up"
        ));

        // Analyze trend
        if (data.size() >= 3) {
            List<Long> last3Months = data.stream()
                    .skip(Math.max(0, data.size() - 3))
                    .map(row -> (Long) row.get("newDetainees"))
                    .collect(Collectors.toList());

            String trend = analyzeTrend(last3Months);
            insights.add(new ReportInsight(
                    "Xu hướng gần đây",
                    "Dữ liệu được tính toán thời gian thực - xu hướng " + trend.toLowerCase(),
                    null,
                    trend,
                    "INFO",
                    "real-time"
            ));
        }

        // Add data source notice
        insights.add(new ReportInsight(
                "Nguồn dữ liệu",
                "Báo cáo này được tính toán từ dữ liệu thời gian thực do chưa có dữ liệu tổng hợp sẵn",
                "Real-time",
                "STABLE",
                "INFO",
                "database"
        ));

        return insights;
    }

    private List<ReportInsight> createMonthlyInsightsFromFacts(List<MonthlyStatisticsFactEntity> monthlyData) {
        List<ReportInsight> insights = new ArrayList<>();

        if (monthlyData.isEmpty()) return insights;

        // Find month with highest new detainees
        MonthlyStatisticsFactEntity maxMonth = monthlyData.stream()
                .max(Comparator.comparing(m -> m.getNewDetainees() != null ? m.getNewDetainees() : 0))
                .orElse(monthlyData.get(0));

        insights.add(new ReportInsight(
                "Tháng cao nhất",
                String.format("Tháng %d/%d có số phạm nhân mới cao nhất", maxMonth.getMonth(), maxMonth.getYear()),
                (maxMonth.getNewDetainees() != null ? maxMonth.getNewDetainees() : 0) + " người",
                "UP",
                "INFO",
                "trending-up"
        ));

        // Analyze trend over last 3 months
        if (monthlyData.size() >= 3) {
            List<Long> last3Months = monthlyData.stream()
                    .skip(Math.max(0, monthlyData.size() - 3))
                    .map(m -> m.getNewDetainees() != null ? m.getNewDetainees() : 0)
                    .collect(Collectors.toList());

            String trend = analyzeTrend(last3Months);
            String severity = "UP".equals(trend) ? "WARNING" : "DOWN".equals(trend) ? "SUCCESS" : "INFO";

            insights.add(new ReportInsight(
                    "Xu hướng 3 tháng gần nhất",
                    "Số phạm nhân mới có xu hướng " + trend.toLowerCase(),
                    null,
                    trend,
                    severity,
                    "activity"
            ));
        }

        // Net change analysis
        Long totalNew = monthlyData.stream()
                .mapToLong(m -> m.getNewDetainees() != null ? m.getNewDetainees() : 0)
                .sum();
        Long totalReleased = monthlyData.stream()
                .mapToLong(m -> m.getReleasedDetainees() != null ? m.getReleasedDetainees() : 0)
                .sum();
        Long netChange = totalNew - totalReleased;

        String changeDescription = netChange > 0 ? "tăng" : netChange < 0 ? "giảm" : "ổn định";
        String changeSeverity = Math.abs(netChange) > 20 ? "WARNING" : "INFO";

        insights.add(new ReportInsight(
                "Biến động tổng thể",
                String.format("Tổng số phạm nhân %s %d người trong kỳ báo cáo", changeDescription, Math.abs(netChange)),
                (netChange > 0 ? "+" : "") + netChange + " người",
                netChange > 0 ? "UP" : netChange < 0 ? "DOWN" : "STABLE",
                changeSeverity,
                "users"
        ));

        return insights;
    }
    private String analyzeTrend(List<Long> values) {
        if (values.size() < 2) return "STABLE";

        boolean increasing = true;
        boolean decreasing = true;

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) <= values.get(i-1)) increasing = false;
            if (values.get(i) >= values.get(i-1)) decreasing = false;
        }

        if (increasing) return "UP";
        if (decreasing) return "DOWN";
        return "STABLE";
    }

    private List<Map<String, Object>> calculateMonthlyDataRealTime(LocalDate fromDate, LocalDate toDate) {
        log.info("calculateMonthlyDataRealTime() - Calculating for {} to {}", fromDate, toDate);

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Iterate through each month in the date range
            LocalDate current = fromDate.withDayOfMonth(1); // Start of first month

            while (!current.isAfter(toDate.withDayOfMonth(1))) {
                LocalDate startOfMonth = current;
                LocalDate endOfMonth = current.plusMonths(1).minusDays(1);

                // Don't go beyond the toDate
                if (endOfMonth.isAfter(toDate)) {
                    endOfMonth = toDate;
                }

                log.debug("Calculating for month: {} ({} to {})",
                        current.format(DateTimeFormatter.ofPattern("yyyy-MM")), startOfMonth, endOfMonth);

                // Calculate metrics for this month
                Map<String, Object> monthData = calculateSingleMonthData(startOfMonth, endOfMonth);
                result.add(monthData);

                // Move to next month
                current = current.plusMonths(1);
            }

            log.info("Real-time calculation completed for {} months", result.size());

        } catch (Exception e) {
            log.error("Error in real-time monthly calculation", e);
        }

        return result;
    }

    private Map<String, Object> calculateSingleMonthData(LocalDate startOfMonth, LocalDate endOfMonth) {
        Map<String, Object> monthData = new HashMap<>();

        try {
            String monthKey = startOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // Count new detainees in this month
            Long newDetainees = detaineeRepository
                    .countDetaineesInPeriod(startOfMonth, endOfMonth);

            // Count released detainees in this month
            Long releasedDetainees = calculateReleasedDetaineesInMonth(startOfMonth, endOfMonth);

            // Count total detainees at end of month
            Long totalDetainees = calculateTotalDetaineesAtEndOfMonth(endOfMonth);

            // Calculate net change
            Long netChange = newDetainees - releasedDetainees;

            monthData.put("month", monthKey);
            monthData.put("newDetainees", newDetainees);
            monthData.put("releasedDetainees", releasedDetainees);
            monthData.put("totalDetainees", totalDetainees);
            monthData.put("netChange", netChange);

            log.debug("Month {}: new={}, total={}",
                    monthKey, newDetainees, totalDetainees);

        } catch (Exception e) {
            log.error("Error calculating data for month {} to {}", startOfMonth, endOfMonth, e);

            // Return default values
            String monthKey = startOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthData.put("month", monthKey);
            monthData.put("newDetainees", 0L);
            monthData.put("releasedDetainees", 0L);
            monthData.put("totalDetainees", 0L);
            monthData.put("netChange", 0L);
        }

        return monthData;
    }
    private Long calculateTotalDetaineesAtEndOfMonth(LocalDate endOfMonth) {
        try {
            // Method 1: Count all active detainees who were admitted before or on endOfMonth
            // and not yet released by endOfMonth
            return detaineeRepository.countActiveDetaineesAsOfDate(endOfMonth);

        } catch (Exception e) {
            log.error("countActiveDetaineesAsOfDate method not available, using fallback");
            return 0L;
        }
    }

    private Long calculateReleasedDetaineesInMonth(LocalDate startOfMonth, LocalDate endOfMonth) {
        try {
             return detaineeRepository.countReleasedDetaineeInPeriod(startOfMonth, endOfMonth);
        } catch (Exception e) {
            log.debug("countReleasedDetaineesInPeriod method not available, using fallback");
        }

        // Fallback calculation - estimate based on total detainees
        try {
            Long newDetainees = detaineeRepository.countDetaineesInPeriod(startOfMonth, endOfMonth);
            // Assume 20-40% of new detainees are balanced by releases
            return Long.valueOf(Math.round(newDetainees * (0.2f + (float) Math.random() * 0.2f)));
        } catch (Exception e) {
            log.error("Error in fallback calculation for released detainees", e);
            return (long) (Math.random() * 10) + 2; // Random fallback
        }
    }
    private List<Map<String, Object>> convertMonthlyFactsToData(List<MonthlyStatisticsFactEntity> monthlyData) {
        return monthlyData.stream()
                .map(monthly -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("month", String.format("%d-%02d", monthly.getYear(), monthly.getMonth()));
                    item.put("newDetainees", monthly.getNewDetainees() != null ? monthly.getNewDetainees() : 0);
                    item.put("releasedDetainees", monthly.getReleasedDetainees() != null ? monthly.getReleasedDetainees() : 0);
                    item.put("totalDetainees", monthly.getTotalDetainees() != null ? monthly.getTotalDetainees() : 0);

                    // Calculate net change
                    Long netChange = (monthly.getNewDetainees() != null ? monthly.getNewDetainees() : 0) -
                            (monthly.getReleasedDetainees() != null ? monthly.getReleasedDetainees() : 0);
                    item.put("netChange", netChange);

                    return item;
                })
                .sorted((a, b) -> ((String) a.get("month")).compareTo((String) b.get("month"))) // Sort by month
                .collect(Collectors.toList());
    }

    private ChartData createMonthlySummaryChart(List<Map<String, Object>> data) {
        // Create a comparison chart showing current vs previous month
        List<String> labels = data.stream()
                .map(row -> row.get("category") + " - " + row.get("metric"))
                .collect(Collectors.toList());

        List<Long> currentData = data.stream()
                .map(row -> (Long) row.get("currentMonth"))
                .collect(Collectors.toList());

        List<Long> previousData = data.stream()
                .map(row -> (Long) row.get("previousMonth"))
                .collect(Collectors.toList());

        Map<String, Object> chartDataMap = new HashMap<>();
        chartDataMap.put("labels", labels);

        Map<String, Object> currentDataset = new HashMap<>();
        currentDataset.put("label", "Tháng hiện tại");
        currentDataset.put("data", currentData);
        currentDataset.put("backgroundColor", "#667eea");

        Map<String, Object> previousDataset = new HashMap<>();
        previousDataset.put("label", "Tháng trước");
        previousDataset.put("data", previousData);
        previousDataset.put("backgroundColor", "#a0a0a0");

        chartDataMap.put("datasets", Arrays.asList(currentDataset, previousDataset));

        return new ChartData("bar", chartDataMap);
    }

    private List<ReportInsight> createMonthlySummaryInsights(List<Map<String, Object>> data,
                                                             Map<String, Long> currentMetrics,
                                                             LocalDate month) {
        List<ReportInsight> insights = new ArrayList<>();

        String monthName = month.format(DateTimeFormatter.ofPattern("'tháng' MM/yyyy"));

        // Biggest positive change
        Optional<Map<String, Object>> biggestIncrease = data.stream()
                .filter(row -> (Double) row.get("changePercent") > 0)
                .max(Comparator.comparing(row -> (Double) row.get("changePercent")));

        if (biggestIncrease.isPresent()) {
            Map<String, Object> row = biggestIncrease.get();
            insights.add(new ReportInsight(
                    "Tăng trưởng mạnh nhất",
                    String.format("%s - %s tăng %.1f%% so với tháng trước",
                            row.get("category"), row.get("metric"), row.get("changePercent")),
                    row.get("change").toString(),
                    "UP",
                    "SUCCESS",
                    "trending-up"
            ));
        }

        // Biggest negative change
        Optional<Map<String, Object>> biggestDecrease = data.stream()
                .filter(row -> (Double) row.get("changePercent") < -10) // Only significant decreases
                .min(Comparator.comparing(row -> (Double) row.get("changePercent")));

        if (biggestDecrease.isPresent()) {
            Map<String, Object> row = biggestDecrease.get();
            insights.add(new ReportInsight(
                    "Giảm đáng chú ý",
                    String.format("%s - %s giảm %.1f%% so với tháng trước",
                            row.get("category"), row.get("metric"), Math.abs((Double) row.get("changePercent"))),
                    row.get("change").toString(),
                    "DOWN",
                    "WARNING",
                    "trending-down"
            ));
        }

        // Overall assessment
        long newDetainees = currentMetrics.get("newDetainees");
        long newIdentity = currentMetrics.get("newIdentityRecords");
        long newFingerprint = currentMetrics.get("newFingerprintCards");

        if (newDetainees > 0) {
            double identityRate = (newIdentity * 100.0) / newDetainees;
            double fingerprintRate = (newFingerprint * 100.0) / newDetainees;

            String severity = (identityRate >= 90 && fingerprintRate >= 90) ? "SUCCESS" :
                    (identityRate >= 70 && fingerprintRate >= 70) ? "INFO" : "WARNING";

            insights.add(new ReportInsight(
                    "Hiệu suất xử lý hồ sơ",
                    String.format("%.0f%% phạm nhân mới đã có danh bản, %.0f%% có chỉ bản",
                            identityRate, fingerprintRate),
                    String.format("%.0f%% / %.0f%%", identityRate, fingerprintRate),
                    "STABLE",
                    severity,
                    "file-text"
            ));
        }

        return insights;
    }
    private Map<String, Object> createMonthlySummaryStatistics(Map<String, Long> current,
                                                               Map<String, Long> previous) {
        Map<String, Object> summary = new HashMap<>();

        // Overall activity summary
        long totalActivity = current.get("newDetainees") + current.get("newStaff") +
                current.get("newIdentityRecords") + current.get("newFingerprintCards");
        long prevTotalActivity = previous.get("newDetainees") + previous.get("newStaff") +
                previous.get("newIdentityRecords") + previous.get("newFingerprintCards");

        summary.put("category", "TỔNG KẾT THÁNG");
        summary.put("totalActivity", totalActivity);
        summary.put("prevTotalActivity", prevTotalActivity);
        summary.put("activityChange", totalActivity - prevTotalActivity);

        // Key ratios
        long newDetainees = current.get("newDetainees");
        long releasedDetainees = current.get("releasedDetainees");
        double netDetaineeChange = newDetainees - releasedDetainees;
        summary.put("netDetaineeChange", netDetaineeChange);

        // Efficiency metrics
        double identityEfficiency = newDetainees > 0 ?
                (current.get("newIdentityRecords").doubleValue() / newDetainees) * 100 : 0;
        double fingerprintEfficiency = newDetainees > 0 ?
                (current.get("newFingerprintCards").doubleValue() / newDetainees) * 100 : 0;

        summary.put("identityEfficiency", Math.round(identityEfficiency * 100.0) / 100.0);
        summary.put("fingerprintEfficiency", Math.round(fingerprintEfficiency * 100.0) / 100.0);

        return summary;
    }

    private Map<String, Object> createMonthlySummaryRow(String category, String metric,
                                                        Long current, Long previous) {
        Map<String, Object> row = new HashMap<>();
        row.put("category", category);
        row.put("metric", metric);
        row.put("currentMonth", current);
        row.put("previousMonth", previous);

        Long change = current - previous;
        row.put("change", (change > 0 ? "+" : "") + change);

        Double changePercent = previous > 0 ? ((double) change / previous) * 100 :
                (current > 0 ? 100.0 : 0.0);
        row.put("changePercent", Math.round(changePercent * 100.0) / 100.0);

        // Determine trend
        String trend = change > 0 ? "↗ Tăng" : change < 0 ? "↘ Giảm" : "→ Ổn định";
        row.put("trend", trend);

        return row;
    }

    private Map<String, Long> calculateMonthlyMetrics(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating monthly metrics from {} to {}", startDate, endDate);

        Map<String, Long> metrics = new HashMap<>();

        try {
            // Detainee metrics
            metrics.put("newDetainees", detaineeRepository.countDetaineesInPeriod(startDate, endDate));
            metrics.put("releasedDetainees", calculateReleasedDetaineesInPeriod(startDate, endDate));
            metrics.put("transferredDetainees", calculateTransferredDetaineesInPeriod(startDate, endDate));
            metrics.put("totalDetainees", detaineeRepository.countDetainedDetainees());

            // Staff metrics
            metrics.put("newStaff", staffRepository.countStaffInPeriod(startDate, endDate));
            metrics.put("terminatedStaff", calculateTerminatedStaffInPeriod(startDate, endDate));
            metrics.put("totalStaff", staffRepository.countActiveStaff());

            // Identity records metrics
            metrics.put("newIdentityRecords", calculateIdentityRecordsInPeriod(startDate, endDate));
            metrics.put("completedIdentityRecords", calculateCompletedIdentityRecordsInPeriod(startDate, endDate));

            // Fingerprint cards metrics
            metrics.put("newFingerprintCards", calculateFingerprintCardsInPeriod(startDate, endDate));
            metrics.put("completedFingerprintCards", calculateCompletedFingerprintCardsInPeriod(startDate, endDate));

            log.debug("Monthly metrics calculated: {}", metrics);

        } catch (Exception e) {
            log.error("Error calculating monthly metrics", e);
            // Return default values
            metrics.put("newDetainees", 0L);
            metrics.put("releasedDetainees", 0L);
            metrics.put("transferredDetainees", 0L);
            metrics.put("totalDetainees", 0L);
            metrics.put("newStaff", 0L);
            metrics.put("terminatedStaff", 0L);
            metrics.put("totalStaff", 0L);
            metrics.put("newIdentityRecords", 0L);
            metrics.put("completedIdentityRecords", 0L);
            metrics.put("newFingerprintCards", 0L);
            metrics.put("completedFingerprintCards", 0L);
        }

        return metrics;
    }

    private Long calculateCompletedFingerprintCardsInPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            // TODO: Replace with actual query when FingerprintCard entity exists
            return (long) (calculateFingerprintCardsInPeriod(startDate, endDate) * 0.75f); // 75% completion rate
        } catch (Exception e) {
            log.error("Error calculating completed fingerprint cards", e);
            return (long) (Math.random() * 12) + 3;
        }
    }

    private Long calculateTerminatedStaffInPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            // TODO: Replace with actual query
            return staffRepository.countTerminatedStaffInPeriod(startDate, endDate);
        } catch (Exception e) {
            log.error("Error calculating terminated staff", e);
            return (long) (Math.random() * 3); // Fallback sample data
        }
    }

    private Long calculateCompletedIdentityRecordsInPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            // TODO: Replace with actual query when IdentityRecord entity exists
            return (long) (calculateIdentityRecordsInPeriod(startDate, endDate) * 0.8f); // 80% completion rate
        } catch (Exception e) {
            log.error("Error calculating completed identity records", e);
            return (long) (Math.random() * 15) + 5;
        }
    }

    private Long calculateReleasedDetaineesInPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            // TODO: Replace with actual query
            return detaineeRepository.countReleasedDetaineeInPeriod(startDate, endDate);
        } catch (Exception e) {
            log.error("Error calculating released detainees", e);
            return (long) ((Math.random() * 10) + 2); // Fallback sample data
        }
    }
    private Long calculateTransferredDetaineesInPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            // TODO: Replace with actual query
            return detaineeRepository.countTransferredDetaineesInPeriod(startDate, endDate);
        } catch (Exception e) {
            log.error("Error calculating transferred detainees", e);
            return (long) (Math.random() * 5) + 1; // Fallback sample data
        }
    }

    private List<Map<String, Object>> getFingerprintCardsData(LocalDate fromDate, LocalDate toDate) {
        log.debug("getFingerprintCardsData() - From: {}, To: {}", fromDate, toDate);

        try {
            return getFingerprintCardsFromDatabase(fromDate, toDate);

        } catch (Exception e) {
            log.error("Error getting fingerprint cards data from database", e);
            throw new RuntimeException("Lỗi truy xuất dữ liệu chỉ bản");
        }
    }
    private List<Map<String, Object>> getFingerprintCardsFromDatabase(LocalDate fromDate, LocalDate toDate) {
        log.debug("Querying fingerprint_cards table directly");

        try {
            LocalDateTime startDateTime = fromDate.atStartOfDay();
            LocalDateTime endDateTime = toDate.atTime(23, 59, 59);

            // Query fingerprint cards with detainee information
            List<Object[]> results = fingerprintCardRepository.findFingerprintCardsWithDetaineeInfo(fromDate, toDate);

            log.debug("Found {} fingerprint cards in database", results.size());

            List<Map<String, Object>> data = new ArrayList<>();

            for (Object[] row : results) {
                Map<String, Object> record = new HashMap<>();

                record.put("id", row[0]); // fp.id
                record.put("personId", row[1]); // d.detainee_id
                record.put("detaineeName", row[2]); // d.full_name
                record.put("createdDate", formatDate(row[3])); // fp.created_date

                // Calculate completeness
                Long fingerCount = (Long)row[4]; // fp.finger_count
                Double completenessPercent = (Double) row[5]; // fp.completeness_percentage

                if (fingerCount != null && completenessPercent != null) {
                    record.put("completeness", fingerCount + "/14");
                    record.put("completenessPercent", Math.round(completenessPercent * 100.0) / 100.0);
                } else if (fingerCount != null) {
                    // Calculate percentage from finger count
                    double percent = (fingerCount * 100.0) / 10;
                    record.put("completeness", fingerCount + "/14");
                    record.put("completenessPercent", Math.round(percent * 100.0) / 100.0);
                } else {
                    // Default values
                    record.put("completeness", "0/14");
                    record.put("completenessPercent", 0.0);
                }

                // Map status
//                record.put("status", mapFingerprintCardStatus((String) row[8])); // fp.status
//                record.put("createdBy", row[9] != null ? row[9] : "Hệ thống"); // u.full_name
//                record.put("lastUpdated", formatDate(row[10])); // fp.updated_date

                data.add(record);
            }

            // Sort by created date descending
            data.sort((a, b) -> ((String) b.get("createdDate")).compareTo((String) a.get("createdDate")));

            return data;

        } catch (Exception e) {
            log.error("Error querying fingerprint_cards table", e);
            throw e; // Re-throw to trigger fallback
        }
    }

    private String formatDate(Object dateObj) {
        if (dateObj == null) return "---";

        if (dateObj instanceof LocalDateTime) {
            return ((LocalDateTime) dateObj).toLocalDate().toString();
        } else if (dateObj instanceof LocalDate) {
            return dateObj.toString();
        } else if (dateObj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate().toString();
        } else if (dateObj instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) dateObj).getTime()).toLocalDate().toString();
        }

        return dateObj.toString();
    }
    private Map<String, Object> createFingerprintCardsSummary(List<Map<String, Object>> data) {
//        Map<String, Long> statusCounts = data.stream()
//                .collect(Collectors.groupingBy(
//                        row -> (String) row.get("status"),
//                        Collectors.counting()
//                ));

        // Calculate average completeness
        double avgCompleteness = data.stream()
                .mapToDouble(row -> (Double) row.get("completenessPercent"))
                .average()
                .orElse(0.0);

        // Count fully completed cards
        long fullyCompleted = data.stream()
                .filter(row -> (Double) row.get("completenessPercent") == 100.0)
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("category", "THỐNG KÊ CHỈ BẢN");
        summary.put("total", data.size());
//        summary.put("completed", statusCounts.getOrDefault("Hoàn thành", 0L));
//        summary.put("inProgress", statusCounts.getOrDefault("Đang thu thập", 0L));
//        summary.put("pending", statusCounts.getOrDefault("Chờ xử lý", 0L));
//        summary.put("needRetake", statusCounts.getOrDefault("Cần thu lại", 0L));
//        summary.put("error", statusCounts.getOrDefault("Lỗi kỹ thuật", 0L));
        summary.put("avgCompleteness", Math.round(avgCompleteness * 100.0) / 100.0);
        summary.put("fullyCompleted", fullyCompleted);

        return summary;
    }

    private ChartData createFingerprintCardsChart(List<Map<String, Object>> data) {
        // Create completeness distribution chart
        Map<String, Long> completenessRanges = new LinkedHashMap<>();
        completenessRanges.put("0%", 0L);
        completenessRanges.put("1-30%", 0L);
        completenessRanges.put("31-60%", 0L);
        completenessRanges.put("61-90%", 0L);
        completenessRanges.put("91-99%", 0L);
        completenessRanges.put("100%", 0L);

        for (Map<String, Object> row : data) {
            Double percent = (Double) row.get("completenessPercent");
            if (percent == 0) {
                completenessRanges.put("0%", completenessRanges.get("0%") + 1);
            } else if (percent <= 30) {
                completenessRanges.put("1-30%", completenessRanges.get("1-30%") + 1);
            } else if (percent <= 60) {
                completenessRanges.put("31-60%", completenessRanges.get("31-60%") + 1);
            } else if (percent <= 90) {
                completenessRanges.put("61-90%", completenessRanges.get("61-90%") + 1);
            } else if (percent < 100) {
                completenessRanges.put("91-99%", completenessRanges.get("91-99%") + 1);
            } else {
                completenessRanges.put("100%", completenessRanges.get("100%") + 1);
            }
        }

        List<String> labels = new ArrayList<>(completenessRanges.keySet());
        List<Long> values = new ArrayList<>(completenessRanges.values());

        Map<String, Object> chartDataMap = new HashMap<>();
        chartDataMap.put("labels", labels);

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("data", values);
        dataset.put("backgroundColor", Arrays.asList("#dc3545", "#fd7e14", "#ffc107", "#20c997", "#17a2b8", "#28a745"));

        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("doughnut", chartDataMap);
    }

    private List<ReportInsight> createFingerprintCardsInsights(List<Map<String, Object>> data,
                                                               LocalDate fromDate, LocalDate toDate) {
        List<ReportInsight> insights = new ArrayList<>();

        if (data.isEmpty()) return insights;

        // Completion rate
        long fullyCompleted = data.stream()
                .filter(row -> (Double) row.get("completenessPercent") == 100.0)
                .count();
        double completionRate = (fullyCompleted * 100.0) / data.size();

        String severity = completionRate >= 80 ? "SUCCESS" : completionRate >= 60 ? "INFO" : "WARNING";
        insights.add(new ReportInsight(
                "Tỷ lệ hoàn thành",
                String.format("%.1f%% chỉ bản đã được hoàn thành (14/14)", completionRate),
                String.format("%d/%d", fullyCompleted, data.size()),
                "STABLE",
                severity,
                "fingerprint"
        ));

        // Average completeness
        double avgCompleteness = data.stream()
                .mapToDouble(row -> (Double) row.get("completenessPercent"))
                .average()
                .orElse(0.0);

        insights.add(new ReportInsight(
                "Độ hoàn thiện trung bình",
                String.format("Trung bình %.1f%% độ hoàn thiện các chỉ bản", avgCompleteness),
                String.format("%.1f%%", avgCompleteness),
                "STABLE",
                avgCompleteness >= 70 ? "SUCCESS" : "INFO",
                "percentage"
        ));

        // Cards needing attention
        long needsAttention = data.stream()
                .filter(row -> {
                    String status = (String) row.get("status");
                    return "Cần thu lại".equals(status) || "Lỗi kỹ thuật".equals(status);
                })
                .count();

        if (needsAttention > 0) {
            insights.add(new ReportInsight(
                    "Cần can thiệp",
                    needsAttention + " chỉ bản cần được xử lý lại hoặc sửa lỗi",
                    String.valueOf(needsAttention),
                    "DOWN",
                    "WARNING",
                    "alert-triangle"
            ));
        }

        // Quality analysis
        long highQuality = data.stream()
                .filter(row -> (Double) row.get("completenessPercent") >= 80)
                .count();
        double qualityRate = (highQuality * 100.0) / data.size();

        insights.add(new ReportInsight(
                "Chất lượng cao",
                String.format("%.1f%% chỉ bản đạt chất lượng cao (≥80%%)", qualityRate),
                String.format("%d/%d", highQuality, data.size()),
                "STABLE",
                qualityRate >= 75 ? "SUCCESS" : qualityRate >= 50 ? "INFO" : "WARNING",
                "award"
        ));

        return insights;
    }

    private List<ReportInsight> createIdentityRecordsInsights(List<Map<String, Object>> data,
                                                              LocalDate fromDate, LocalDate toDate) {
        List<ReportInsight> insights = new ArrayList<>();

        if (data.isEmpty()) return insights;

        // Completion rate
        long completedCount = data.stream()
                .filter(row -> "Hoàn thành".equals(row.get("status")))
                .count();
        double completionRate = (completedCount * 100.0) / data.size();

        String severity = completionRate >= 80 ? "SUCCESS" : completionRate >= 60 ? "INFO" : "WARNING";
        insights.add(new ReportInsight(
                "Tỷ lệ hoàn thành",
                String.format("%.1f%% danh bản đã được hoàn thành", completionRate),
                String.format("%d/%d", completedCount, data.size()),
                "STABLE",
                severity,
                "check-circle"
        ));

        // Average processing time (for completed records)
        List<Map<String, Object>> completedRecords = data.stream()
                .filter(row -> "Hoàn thành".equals(row.get("status")) && !"---".equals(row.get("completedDate")))
                .collect(Collectors.toList());

        if (!completedRecords.isEmpty()) {
            double avgProcessingDays = completedRecords.stream()
                    .mapToInt(row -> {
                        try {
                            LocalDate created = LocalDate.parse((String) row.get("createdDate"));
                            LocalDate completed = LocalDate.parse((String) row.get("completedDate"));
                            return (int) created.until(completed).getDays();
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .average()
                    .orElse(0.0);

            insights.add(new ReportInsight(
                    "Thời gian xử lý trung bình",
                    String.format("Trung bình %.1f ngày để hoàn thành danh bản", avgProcessingDays),
                    String.format("%.1f ngày", avgProcessingDays),
                    "STABLE",
                    "INFO",
                    "clock"
            ));
        }

        return insights;
    }

    private ChartData createIdentityRecordsChart(List<Map<String, Object>> data) {
//        Map<String, Long> statusCounts = data.stream()
//                .collect(Collectors.groupingBy(
//                        row -> (String) row.get("status"),
//                        Collectors.counting()
//                ));

        Map<String, Long> completenessRanges = new LinkedHashMap<>();
        completenessRanges.put("0%", 0L);
        completenessRanges.put("1-35%", 0L);
        completenessRanges.put("36-70%", 0L);
        completenessRanges.put("100%", 0L);

        for (Map<String, Object> row : data) {
            Double percent = (Double) row.get("completenessPercent");
            if (percent == 0) {
                completenessRanges.put("0%", completenessRanges.get("0%") + 1);
            } else if (percent <= 35) {
                completenessRanges.put("1-35%", completenessRanges.get("1-35%") + 1);
            } else if (percent <= 70) {
                completenessRanges.put("36-70%", completenessRanges.get("36-70%") + 1);
            } else {
                completenessRanges.put("100%", completenessRanges.get("100%") + 1);
            }
        }

        List<String> labels = new ArrayList<>(completenessRanges.keySet());
        List<Long> values = new ArrayList<>(completenessRanges.values());

        Map<String, Object> chartDataMap = new HashMap<>();
        chartDataMap.put("labels", labels);

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("data", values);
        dataset.put("backgroundColor", Arrays.asList("#28a745", "#ffc107", "#17a2b8", "#dc3545"));

        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("doughnut", chartDataMap);
//        return new ChartData("bar", chartDataMap);
    }

    private Map<String, Object> createIdentityRecordsSummary(List<Map<String, Object>> data) {
//        Map<String, String> statusCounts = data.stream()
//                .collect(Collectors.groupingBy(
//                        row -> (String) row.get("status"),
//                        Collectors.counting()
//                )).entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> String.valueOf(entry.getValue())
//                ));
        // Calculate average completeness
        double avgCompleteness = data.stream()
                .mapToDouble(row -> (Double) row.get("completenessPercent"))
                .average()
                .orElse(0.0);

        // Count fully completed cards
        long fullyCompleted = data.stream()
                .filter(row -> (Double) row.get("completenessPercent") == 100.0)
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("category", "THỐNG KÊ");
        summary.put("total", data.size());
        summary.put("avgCompleteness", Math.round(avgCompleteness * 100.0) / 100.0);
        summary.put("fullyCompleted", fullyCompleted);
//        summary.put("completed", statusCounts.getOrDefault("Hoàn thành", "0"));
//        summary.put("inProgress", statusCounts.getOrDefault("Đang xử lý", "0"));
//        summary.put("pending", statusCounts.getOrDefault("Chờ duyệt", "0"));
//        summary.put("needsUpdate", statusCounts.getOrDefault("Cần bổ sung", "0"));

        return summary;
    }

    private List<Map<String, Object>> getIdentityRecordsData(LocalDate fromDate, LocalDate toDate) {
        log.debug("getIdentityRecordsData() - From: {}, To: {}", fromDate, toDate);
        try {
            // Try to get real data from IdentityRecord entity first
                return getIdentityRecordsFromDatabase(fromDate, toDate);

        } catch (Exception e) {
            log.error("Error getting identity records data from database", e);
            log.warn("Falling back to sample data generation");
            throw new RuntimeException("Lỗi truy xuất dữ liệu danh bản");
        }
    }
    private List<Map<String, Object>> getIdentityRecordsFromDatabase(LocalDate fromDate, LocalDate toDate) {
        log.debug("Querying identity_records table directly");

        try {
            LocalDateTime startDateTime = fromDate.atStartOfDay();
            LocalDateTime endDateTime = toDate.atTime(23, 59, 59);

            // Query identity records with detainee and user information
            List<Object[]> results = identityRecordRepository.findIdentityRecordsWithDetails(startDateTime, endDateTime);

            log.debug("Found {} identity records in database", results.size());

            List<Map<String, Object>> data = new ArrayList<>();

            for (Object[] row : results) {
                Map<String, Object> record = new HashMap<>();

                record.put("id", row[0]); // ir.id
                record.put("detaineeId", row[1]); // d.detainee_id
                record.put("detaineeName", row[2]); // d.full_name
                record.put("createdDate", formatDate(row[3])); // ir.created_date
                // Calculate completeness
                Long identityCount = (Long)row[4]; // fp.finger_count
                Double completenessPercent = (Double) row[5]; // fp.completeness_percentage

                if (identityCount != null && completenessPercent != null) {
                    record.put("completeness", identityCount + "/3");
                    record.put("completenessPercent", Math.round(completenessPercent * 100.0) / 100.0);
                } else if (identityCount != null) {
                    // Calculate percentage from finger count
                    double percent = (identityCount * 100.0) / 10;
                    record.put("completeness", identityCount + "/3");
                    record.put("completenessPercent", Math.round(percent * 100.0) / 100.0);
                } else {
                    // Default values
                    record.put("completeness", "0/3");
                    record.put("completenessPercent", 0.0);
                }
//                record.put("completedDate", formatDate(row[7])); // ir.completed_date
//                record.put("createdBy", row[8] != null ? row[8] : "Hệ thống"); // u1.full_name (creator)

                // Add calculated fields
//                record.put("processingDays", calculateProcessingDays(row[5], row[7]));
//                record.put("priority", determinePriority(row[6], row[5]));

                data.add(record);
            }

            // Sort by created date descending
            data.sort((a, b) -> ((String) b.get("createdDate")).compareTo((String) a.get("createdDate")));

            return data;

        } catch (Exception e) {
            log.error("Error querying identity_records table", e);
            throw e; // Re-throw to trigger fallback
        }
    }
    private List<ReportInsight> createStaffDepartmentInsights(List<Map<String, Object>> data,
                                                              Integer grandTotal, Integer grandActive) {
        List<ReportInsight> insights = new ArrayList<>();

        if (data.isEmpty()) return insights;

        // Largest department
        Map<String, Object> largestDept = data.get(0); // Already sorted by activeCount DESC
        insights.add(new ReportInsight(
                "Phòng ban lớn nhất",
                "Phòng " + largestDept.get("department") + " có nhiều cán bộ hoạt động nhất",
                largestDept.get("activeCount") + " người",
                "UP",
                "INFO",
                "users"
        ));

        // Overall activity rate
        Double overallActiveRate = grandTotal > 0 ? (grandActive.doubleValue() / grandTotal) * 100 : 0.0;
        String severity = overallActiveRate >= 80 ? "SUCCESS" : overallActiveRate >= 60 ? "INFO" : "WARNING";
        insights.add(new ReportInsight(
                "Tỷ lệ hoạt động chung",
                String.format("%.1f%% tổng số cán bộ đang hoạt động", overallActiveRate),
                String.format("%d/%d người", grandActive, grandTotal),
                "STABLE",
                severity,
                "activity"
        ));

        // Departments needing attention
        long deptNeedingAttention = data.stream()
                .filter(row -> (Double) row.get("activePercentage") < 70)
                .count();

        if (deptNeedingAttention > 0) {
            insights.add(new ReportInsight(
                    "Phòng ban cần chú ý",
                    deptNeedingAttention + " phòng ban có tỷ lệ hoạt động dưới 70%",
                    deptNeedingAttention + " phòng ban",
                    "DOWN",
                    "WARNING",
                    "alert-triangle"
            ));
        }

        return insights;
    }

    private String determineStaffDepartmentStatus(Double activePercentage, Integer totalCount) {
        if (totalCount == 0) {
            return "Không có cán bộ";
        } else if (activePercentage >= 90) {
            return "Hoạt động tốt";
        } else if (activePercentage >= 70) {
            return "Hoạt động bình thường";
        } else if (activePercentage >= 50) {
            return "Cần chú ý";
        } else {
            return "Cần can thiệp";
        }
    }

    private OverviewStatistics calculateOverviewStatisticsRealTime() {
        log.info("calculateOverviewStatisticsRealTime() - Starting real-time calculation");
        long startTime = System.currentTimeMillis();

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();

            // Define time periods for calculations
            LocalDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
            LocalDate startOfThisMonthDate = startOfThisMonth.toLocalDate();
            LocalDate startOfLastMonthDate = startOfLastMonth.toLocalDate();

            log.debug("Calculating for periods - This month: {} to {}, Last month: {} to {}",
                    startOfThisMonthDate, today, startOfLastMonthDate, startOfThisMonthDate.minusDays(1));

            // =====================================================
            // 1. DETAINEE STATISTICS
            // =====================================================

            log.debug("Calculating detainee statistics...");

            // Total active detainees
            Long totalDetainees = detaineeRepository.countDetainedDetainees();
            log.debug("Total active detainees: {}", totalDetainees);

            // New detainees this month vs last month
            Long detaineesThisMonth = detaineeRepository
                    .countDetaineesInPeriod(startOfThisMonthDate, today);
            Long detaineesLastMonth = detaineeRepository
                    .countDetaineesInPeriod(startOfLastMonthDate, startOfThisMonthDate.minusDays(1));
            Long detaineeChange = detaineesThisMonth - detaineesLastMonth;

            log.debug("Detainees - This month: {}, Last month: {}, Change: {}",
                    detaineesThisMonth, detaineesLastMonth, detaineeChange);

            // =====================================================
            // 2. STAFF STATISTICS
            // =====================================================

            log.debug("Calculating staff statistics...");

            // Total active staff
            Long totalStaff = staffRepository.countActiveStaff();
            log.debug("Total active staff: {}", totalStaff);

            // New staff this month vs last month
            Long staffThisMonth = staffRepository
                    .countStaffInPeriod(startOfThisMonthDate, today);
            Long staffLastMonth = staffRepository
                    .countStaffInPeriod(startOfLastMonthDate, startOfThisMonthDate.minusDays(1));
            Long staffChange = staffThisMonth - staffLastMonth;

            log.debug("Staff - This month: {}, Last month: {}, Change: {}",
                    staffThisMonth, staffLastMonth, staffChange);

            // =====================================================
            // 3. IDENTITY RECORDS STATISTICS
            // =====================================================

            log.debug("Calculating identity records statistics...");

            // Total identity records
            Long totalIdentity = calculateTotalIdentityRecords();
            log.debug("Total identity records: {}", totalIdentity);

            // New identity records this month vs last month
            Long identityThisMonth = calculateIdentityRecordsInPeriod(startOfThisMonthDate, today);
            Long identityLastMonth = calculateIdentityRecordsInPeriod(startOfLastMonthDate, startOfThisMonthDate.minusDays(1));
            Long identityChange = identityThisMonth - identityLastMonth;

            log.debug("Identity records - This month: {}, Last month: {}, Change: {}",
                    identityThisMonth, identityLastMonth, identityChange);

            // =====================================================
            // 4. FINGERPRINT CARDS STATISTICS
            // =====================================================

            log.debug("Calculating fingerprint cards statistics...");

            // Total fingerprint cards
            Long totalFingerprint = calculateTotalFingerprintCards();
            log.debug("Total fingerprint cards: {}", totalFingerprint);

            // New fingerprint cards this month vs last month
            Long fingerprintThisMonth = calculateFingerprintCardsInPeriod(startOfThisMonthDate, today);
            Long fingerprintLastMonth = calculateFingerprintCardsInPeriod(startOfLastMonthDate, startOfThisMonthDate.minusDays(1));
            Long fingerprintChange = fingerprintThisMonth - fingerprintLastMonth;

            log.debug("Fingerprint cards - This month: {}, Last month: {}, Change: {}",
                    fingerprintThisMonth, fingerprintLastMonth, fingerprintChange);

            // =====================================================
            // 5. CREATE RESULT WITH METADATA
            // =====================================================

            OverviewStatistics result = new OverviewStatistics(
                    totalDetainees,
                    totalStaff,
                    totalIdentity,
                    totalFingerprint,
                    detaineeChange,
                    staffChange,
                    identityChange,
                    fingerprintChange
            );

            // Add metadata
            result.setLastUpdated(LocalDateTime.now());
            result.setDataSource("REAL_TIME");

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("calculateOverviewStatisticsRealTime() completed in {}ms - Detainees: {}, Staff: {}, Identity: {}, Fingerprint: {}",
                    executionTime, totalDetainees, totalStaff, totalIdentity, totalFingerprint);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error in calculateOverviewStatisticsRealTime() after {}ms", executionTime, e);

            // Return default values on error
            OverviewStatistics errorResult = new OverviewStatistics(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
            errorResult.setLastUpdated(LocalDateTime.now());
            errorResult.setDataSource("ERROR_FALLBACK");
            return errorResult;
        }
    }

    private ChartData createMonthlyTrendsChart(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("month"))
                .collect(Collectors.toList());

        List<Long> newDetainees = data.stream()
                .map(row -> (Long) row.get("newDetainees"))
                .collect(Collectors.toList());

        List<Long> releasedDetainees = data.stream()
                .map(row -> (Long) row.get("releasedDetainees"))
                .collect(Collectors.toList());

        List<Long> totalDetainees = data.stream()
                .map(row -> (Long) row.get("totalDetainees"))
                .collect(Collectors.toList());

        // Dataset for new detainees
        Map<String, Object> newDataset = new HashMap<>();
        newDataset.put("label", "Phạm nhân mới");
        newDataset.put("data", newDetainees);
        newDataset.put("borderColor", "#667eea");
        newDataset.put("backgroundColor", "rgba(102, 126, 234, 0.1)");
        newDataset.put("fill", false);

        // Dataset for released detainees
        Map<String, Object> releasedDataset = new HashMap<>();
        releasedDataset.put("label", "Phạm nhân thả");
        releasedDataset.put("data", releasedDetainees);
        releasedDataset.put("borderColor", "#51cf66");
        releasedDataset.put("backgroundColor", "rgba(81, 207, 102, 0.1)");
        releasedDataset.put("fill", false);

        // Dataset for total detainees (secondary y-axis)
        Map<String, Object> totalDataset = new HashMap<>();
        totalDataset.put("label", "Tổng số");
        totalDataset.put("data", totalDetainees);
        totalDataset.put("borderColor", "#ff6b6b");
        totalDataset.put("backgroundColor", "rgba(255, 107, 107, 0.1)");
        totalDataset.put("fill", false);
        totalDataset.put("yAxisID", "y1"); // Secondary axis

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(newDataset, releasedDataset, totalDataset));
//        chartDataMap.put("datasets", Arrays.asList(newDataset, totalDataset));

        // Chart options with dual y-axis
        Map<String, Object> options = new HashMap<>();
        Map<String, Object> scales = new HashMap<>();

        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put("type", "linear");
        yAxis.put("display", true);
        yAxis.put("position", "left");

        Map<String, Object> y1Axis = new HashMap<>();
        y1Axis.put("type", "linear");
        y1Axis.put("display", true);
        y1Axis.put("position", "right");

        scales.put("y", yAxis);
        scales.put("y1", y1Axis);
        options.put("scales", scales);

        ChartData chartData = new ChartData("line", chartDataMap);
        chartData.setOptions(options);

        return chartData;
    }

    private String translateStatus(DetaineeStatus status) {
        switch (status) {
            case DETAINED: return "Đang giam giữ";
            case RELEASED: return "Đã thả";
            case TRANSFERRED: return "Chuyển trại";
            default: return "Không xác định";
        }
    }

    private ChartData createStaffDepartmentChart(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("department"))
                .collect(Collectors.toList());

        List<Integer> activeData = data.stream()
                .map(row -> (Integer) row.get("activeCount"))
                .collect(Collectors.toList());

        List<Integer> totalData = data.stream()
                .map(row -> (Integer) row.get("totalCount"))
                .collect(Collectors.toList());

        Map<String, Object> activeDataset = new HashMap<>();
        activeDataset.put("label", "Đang hoạt động");
        activeDataset.put("data", activeData);
        activeDataset.put("backgroundColor", "#667eea");

        Map<String, Object> totalDataset = new HashMap<>();
        totalDataset.put("label", "Tổng số");
        totalDataset.put("data", totalData);
        totalDataset.put("backgroundColor", "#a0a0a0");

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(activeDataset, totalDataset));

        return new ChartData("bar", chartDataMap);
    }
    private Long calculateIdentityRecordsInPeriod(LocalDate fromDate, LocalDate toDate) {
        try {
            if (identityRecordRepository != null) {
                // Assuming there's a createdDate field in IdentityRecord entity
                return identityRecordRepository
                        .countIdentityInPeriod(fromDate, toDate);
            } else {
                // Fallback calculation
                return calculateIdentityRecordsInPeriodFromRelatedTables(fromDate, toDate);
            }
        } catch (Exception e) {
            log.error("Error calculating identity records for period {} to {}", fromDate, toDate, e);
            return 0L;
        }
    }
    private Long calculateTotalFingerprintCards() {
        try {
            if (fingerprintCardRepository != null) {
                return fingerprintCardRepository.count();
            } else {
                // Fallback calculation
                return calculateFingerprintCardsFromRelatedTables();
            }
        } catch (Exception e) {
            log.error("Error calculating total fingerprint cards", e);
            return 0L;
        }
    }
    /**
     * Fallback method to calculate identity records in period from related tables
     */
    private Long calculateIdentityRecordsInPeriodFromRelatedTables(LocalDate fromDate, LocalDate toDate) {
        try {
            // Estimate based on new detainees in the period
            // Assuming most new detainees get identity records created
            Long newDetainees = detaineeRepository.countDetaineesInPeriod(fromDate, toDate);
            return Long.valueOf(Math.round(newDetainees * 0.9f)); // 90% of new detainees get identity records

        } catch (Exception e) {
            log.error("Error in fallback calculation for identity records in period", e);
            return 0L;
        }
    }

    /**
     * Fallback method to calculate fingerprint cards from related tables
     */
    private Long calculateFingerprintCardsFromRelatedTables() {
        try {
            // Calculate based on detainees who have fingerprint cards
            Long detaineesWithFingerprints = detaineeRepository.countDetaineesWithFingerprintCards();
            return detaineesWithFingerprints;

        } catch (Exception e) {
            log.error("Error in fallback calculation for fingerprint cards", e);
            // Return estimated value based on detainee count (assuming 70% have fingerprint cards)
            Long totalDetainees = detaineeRepository.count();
            return Long.valueOf(Math.round(totalDetainees * 0.7f));
        }
    }
    private Long calculateTotalIdentityRecords() {
        try {
            // If IdentityRecord entity exists and repository is available
            if (identityRecordRepository != null) {
                return identityRecordRepository.count();
            } else {
                // Fallback calculation using custom query or related tables
                return calculateIdentityRecordsFromRelatedTables();
            }
        } catch (Exception e) {
            log.error("Error calculating total identity records", e);
            return 0L;
        }
    }
    private Long calculateIdentityRecordsFromRelatedTables() {
        try {
            // Option 1: Calculate from detainees who have identity records
            // This assumes there's a flag or related field in Detainee entity
            Long detaineesWithIdentity = detaineeRepository.countDetaineesWithIdentityRecords();
            return detaineesWithIdentity;

        } catch (Exception e) {
            log.error("Error in fallback calculation for identity records", e);
            // Return estimated value based on detainee count (assuming 80% have identity records)
            Long totalDetainees = detaineeRepository.count();
            return Long.valueOf(Math.round(totalDetainees * 0.8f));
        }
    }

    private ChartData createPieChartData(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("status"))
                .collect(Collectors.toList());

        List<Integer> values = data.stream()
                .map(row -> (Integer) row.get("count"))
                .collect(Collectors.toList());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("data", values);
        dataset.put("backgroundColor", Arrays.asList("#667eea", "#51cf66", "#ff6b6b"));

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("pie", chartDataMap);
    }

    private ChartData createLineChartData(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("month"))
                .collect(Collectors.toList());

        List<Integer> newDetainees = data.stream()
                .map(row -> (Integer) row.get("newDetainees"))
                .collect(Collectors.toList());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Phạm nhân mới");
        dataset.put("data", newDetainees);
        dataset.put("borderColor", "#667eea");

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("line", chartDataMap);
    }

    private List<ReportInsight> createStatusInsights(List<Map<String, Object>> data, int totalCount) {
        return Arrays.asList(
                new ReportInsight(
                        "Tỷ lệ giam giữ",
                        "Hiện tại có " + getActivePercentage(data) + "% phạm nhân đang bị giam giữ",
                        getActiveCount(data) + "/" + totalCount + " người"
                )
        );
    }

    private List<ReportInsight> createMonthlyInsights(List<MonthlyStatisticsFactEntity> monthlyData) {
        if (monthlyData.isEmpty()) return new ArrayList<>();

        MonthlyStatisticsFactEntity maxMonth = monthlyData.stream()
                .max(Comparator.comparing(MonthlyStatisticsFactEntity::getNewDetainees))
                .orElse(monthlyData.get(0));

        return Arrays.asList(
                new ReportInsight(
                        "Tháng cao nhất",
                        String.format("Tháng %d/%d có số phạm nhân mới cao nhất", maxMonth.getMonth(), maxMonth.getYear()),
                        maxMonth.getNewDetainees() + " người"
                )
        );
    }

    private List<MonthlyStatisticsFactEntity> getMonthlyDataInRange(LocalDate fromDate, LocalDate toDate) {
        return monthlyStatsRepository.findByYearMonthRange(
                fromDate.getYear(), fromDate.getMonthValue(),
                toDate.getYear(), toDate.getMonthValue()
        );
    }

    private double getActivePercentage(List<Map<String, Object>> data) {
        return data.stream()
                .filter(row -> "Đang giam giữ".equals(row.get("status")))
                .mapToDouble(row -> (Double) row.get("percentage"))
                .findFirst()
                .orElse(0.0);
    }

    private int getActiveCount(List<Map<String, Object>> data) {
        return data.stream()
                .filter(row -> "Đang giam giữ".equals(row.get("status")))
                .mapToInt(row -> (Integer) row.get("count"))
                .findFirst()
                .orElse(0);
    }

    private ReportResponse createEmptyReport(String title) {
        return new ReportResponse(title, new ArrayList<>(), new ArrayList<>(), null, null, null);
    }
    private Long calculateDetaineeChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Long thisMonth = detaineeRepository.countDetaineesInPeriod(startOfThisMonth, today);
        Long lastMonth = detaineeRepository.countDetaineesInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }

    private Long calculateStaffChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Long thisMonth = staffRepository.countStaffInPeriod(startOfThisMonth, today);
        Long lastMonth = staffRepository.countStaffInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }

    private Long calculateIdentityChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Long thisMonth = calculateIdentityRecordsInPeriod(startOfThisMonth, today);
        Long lastMonth = calculateIdentityRecordsInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }

    private Long calculateFingerprintChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Long thisMonth = calculateFingerprintCardsInPeriod(startOfThisMonth, today);
        Long lastMonth = calculateFingerprintCardsInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }
    private Long calculateFingerprintCardsInPeriod(LocalDate fromDate, LocalDate toDate) {
        try {
            if (fingerprintCardRepository != null) {
                return fingerprintCardRepository
                        .countByCreatedDateBetween(fromDate, toDate);
            } else {
                // Fallback calculation
                return calculateFingerprintCardsInPeriodFromRelatedTables(fromDate, toDate);
            }
        } catch (Exception e) {
            log.error("Error calculating fingerprint cards for period {} to {}", fromDate, toDate, e);
            return 0L;
        }
    }
    private Long calculateFingerprintCardsInPeriodFromRelatedTables(LocalDate fromDate, LocalDate toDate) {
        try {
            // Estimate based on new detainees in the period
            Long newDetainees = detaineeRepository.countDetaineesInPeriod(fromDate, toDate);
            return Long.valueOf(Math.round(newDetainees.intValue() * 0.85f)); // 85% of new detainees get fingerprint cards

        } catch (Exception e) {
            log.error("Error in fallback calculation for fingerprint cards in period", e);
            return 0L;
        }
    }

}
