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
import java.util.*;
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

    // Helper methods
    private OverviewStatistics calculateOverviewStatisticsRealTime() {
        Long totalDetainees = detaineeRepository.countDetainedDetainees();
        Long totalStaff = staffRepository.countActiveStaff();
        // Add other calculations...

        return new OverviewStatistics(totalDetainees, totalStaff, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    private String translateStatus(DetaineeStatus status) {
        switch (status) {
            case DETAINED: return "Đang giam giữ";
            case RELEASED: return "Đã thả";
            case TRANSFERRED: return "Chuyển trại";
            default: return "Không xác định";
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
}
