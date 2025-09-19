package vn.mk.eid.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DailyStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.MonthlyStatisticsFactEntity;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.web.constant.DetaineeStatus;
import vn.mk.eid.web.dto.report.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
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
                        item.put("status", translateStatus((DetaineeStatus) row[0]));
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
    public ReportResponse getDetaineesByMonthReport(LocalDate fromDate, LocalDate toDate) {
        try {
            // Sử dụng pre-aggregated monthly data
            List<MonthlyStatisticsFactEntity> monthlyData = getMonthlyDataInRange(fromDate, toDate);

            List<ReportColumn> columns = Arrays.asList(
                    new ReportColumn("month", "Tháng", "text"),
                    new ReportColumn("newDetainees", "Phạm nhân mới", "number"),
                    new ReportColumn("totalDetainees", "Tổng cuối tháng", "number")
            );

            List<Map<String, Object>> data = monthlyData.stream()
                    .map(monthly -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("month", String.format("%d-%02d", monthly.getYear(), monthly.getMonth()));
                        item.put("newDetainees", monthly.getNewDetainees());
                        item.put("totalDetainees", monthly.getTotalDetainees());
                        return item;
                    })
                    .collect(Collectors.toList());

            // Create chart data for trends
            ChartData chartData = createLineChartData(data);

            // Create insights
            List<ReportInsight> insights = createMonthlyInsights(monthlyData);

            return new ReportResponse(
                    "Báo Cáo Phạm Nhân Theo Tháng",
                    columns, data, null, chartData, insights
            );

        } catch (Exception e) {
            log.error("Error generating detainees by month report", e);
            return createEmptyReport("Lỗi tạo báo cáo");
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
            Integer totalStaff = staffRepository.countActiveStaff().intValue();
            log.debug("Total active staff: {}", totalStaff);

            // New staff this month vs last month
            Integer staffThisMonth = staffRepository
                    .countStaffInPeriod(startOfThisMonthDate, today).intValue();
            Integer staffLastMonth = staffRepository
                    .countStaffInPeriod(startOfLastMonthDate, startOfThisMonthDate.minusDays(1)).intValue();
            Integer staffChange = staffThisMonth - staffLastMonth;

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
            Integer totalFingerprint = calculateTotalFingerprintCards();
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

    private String translateStatus(DetaineeStatus status) {
        switch (status) {
            case DETAINED: return "Đang giam giữ";
            case RELEASED: return "Đã thả";
            case TRANSFERRED: return "Chuyển trại";
            default: return "Không xác định";
        }
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
            return 0;
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
    private Integer calculateDetaineeChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Integer thisMonth = detaineeRepository.countDetaineesInPeriod(startOfThisMonth, today).intValue();
        Integer lastMonth = detaineeRepository.countDetaineesInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1)).intValue();
        return thisMonth - lastMonth;
    }

    private Integer calculateStaffChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Integer thisMonth = staffRepository.countStaffInPeriod(startOfThisMonth, today).intValue();
        Integer lastMonth = staffRepository.countStaffInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1)).intValue();
        return thisMonth - lastMonth;
    }

    private Integer calculateIdentityChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Integer thisMonth = calculateIdentityRecordsInPeriod(startOfThisMonth, today);
        Integer lastMonth = calculateIdentityRecordsInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }

    private Integer calculateFingerprintChange(LocalDate startOfThisMonth, LocalDate today, LocalDate startOfLastMonth) {
        Integer thisMonth = calculateFingerprintCardsInPeriod(startOfThisMonth, today);
        Integer lastMonth = calculateFingerprintCardsInPeriod(startOfLastMonth, startOfThisMonth.minusDays(1));
        return thisMonth - lastMonth;
    }
    private Integer calculateFingerprintCardsInPeriod(LocalDate fromDate, LocalDate toDate) {
        try {
            if (fingerprintCardRepository != null) {
                return fingerprintCardRepository
                        .countByCreatedDateBetween(fromDate.atStartOfDay(), toDate.atTime(23, 59, 59))
                        .intValue();
            } else {
                // Fallback calculation
                return calculateFingerprintCardsInPeriodFromRelatedTables(fromDate, toDate);
            }
        } catch (Exception e) {
            log.error("Error calculating fingerprint cards for period {} to {}", fromDate, toDate, e);
            return 0;
        }
    }
    private Integer calculateFingerprintCardsInPeriodFromRelatedTables(LocalDate fromDate, LocalDate toDate) {
        try {
            // Estimate based on new detainees in the period
            Long newDetainees = detaineeRepository.countDetaineesInPeriod(fromDate, toDate);
            return Math.round(newDetainees.intValue() * 0.85f); // 85% of new detainees get fingerprint cards

        } catch (Exception e) {
            log.error("Error in fallback calculation for fingerprint cards in period", e);
            return 0;
        }
    }

}
