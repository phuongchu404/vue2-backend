package vn.mk.eid.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DailyStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.entity.DepartmentStatisticsFactEntity;
import vn.mk.eid.common.dao.entity.MonthlyStatisticsFactEntity;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.web.constant.DetaineeStatus;
import vn.mk.eid.web.dto.report.*;

import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseReportService implements ReportService{

    private final DailyStatisticsFactRepository dailyStatsRepository;


    private MonthlyStatisticsFactRepository monthlyStatsRepository;


    private DetaineeRepository detaineeRepository;


    private StaffRepository staffRepository;

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

                Integer detaineeChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewDetainees).orElse(0);
                Integer staffChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewStaff).orElse(0);
                Integer identityChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewIdentityRecords).orElse(0);
                Integer fingerprintChange = currentMonth.map(MonthlyStatisticsFactEntity::getNewFingerprintCards).orElse(0);

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
            return new OverviewStatistics(0, 0, 0, 0, 0, 0, 0, 0);
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
            List<MonthlyStatisticsFact> monthlyData = getMonthlyDataInRange(fromDate, toDate);

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
        Integer totalDetainees = detaineeRepository.countDetainedDetainees().intValue();
        Integer totalStaff = staffRepository.countActiveStaff().intValue();
        return new OverviewStatistics(totalDetainees, totalStaff, 0, 0, 0, 0, 0, 0);
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

    // Helper methods for department reports
    private ReportResponse getStaffByDepartmentReportFallback() {
        // Fallback to direct query if pre-aggregated data not available
        List<Object[]> results = staffRepository.getStaffByDepartmentStatistics();

        List<ReportColumn> columns = Arrays.asList(
                new ReportColumn("department", "Phòng Ban", "text"),
                new ReportColumn("count", "Số Lượng", "number"),
                new ReportColumn("active", "Đang làm việc", "number")
        );

        List<Map<String, Object>> data = results.stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("department", row[0]); // department name
                    item.put("count", ((Number) row[1]).intValue()); // total count
                    item.put("active", ((Number) row[2]).intValue()); // active count
                    return item;
                })
                .collect(Collectors.toList());

        List<ReportInsight> insights = createDepartmentInsights(data);

        return new ReportResponse("Báo Cáo Cán Bộ Theo Phòng Ban", columns, data,  insights);
    }

    private List<ReportInsight> createDepartmentInsights(List<Map<String, Object>> data) {
        if (data.isEmpty()) return new ArrayList<>();

        // Find department with most staff
        Map<String, Object> maxDept = data.stream()
                .max(Comparator.comparing(row -> (Integer) row.get("active")))
                .orElse(data.get(0));

        // Calculate total active staff across all departments
        int totalActive = data.stream().mapToInt(row -> (Integer) row.get("active")).sum();

        return Arrays.asList(
                new ReportInsight(
                        "Phòng ban lớn nhất",
                        "Phòng " + maxDept.get("department") + " có số cán bộ hoạt động đông nhất",
                        maxDept.get("active") + " người"
                ),
                new ReportInsight(
                        "Tổng cán bộ hoạt động",
                        "Tổng cộng có " + totalActive + " cán bộ đang hoạt động",
                        totalActive + " người"
                )
        );
    }

    private ChartData createDepartmentTrendChart(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("date"))
                .collect(Collectors.toList());

        List<Integer> staffCounts = data.stream()
                .map(row -> (Integer) row.get("activeStaffCount"))
                .collect(Collectors.toList());

        List<Integer> detaineeAssigned = data.stream()
                .map(row -> (Integer) row.get("detaineesAssigned"))
                .collect(Collectors.toList());

        Map<String, Object> dataset1 = new HashMap<>();
        dataset1.put("label", "Cán bộ hoạt động");
        dataset1.put("data", staffCounts);
        dataset1.put("borderColor", "#667eea");

        Map<String, Object> dataset2 = new HashMap<>();
        dataset2.put("label", "Phạm nhân được giao");
        dataset2.put("data", detaineeAssigned);
        dataset2.put("borderColor", "#51cf66");

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(dataset1, dataset2));

        return new ChartData("line", chartDataMap);
    }

    private ChartData createDepartmentComparisonChart(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("department"))
                .collect(Collectors.toList());

        List<Integer> activeStaff = data.stream()
                .map(row -> (Integer) row.get("activeStaffCount"))
                .collect(Collectors.toList());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Cán bộ hoạt động");
        dataset.put("data", activeStaff);
        dataset.put("backgroundColor", Arrays.asList("#667eea", "#51cf66", "#ff6b6b", "#ffd43b", "#845ef7"));

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("bar", chartDataMap);
    }

    private List<ReportInsight> createDepartmentTrendInsights(List<DepartmentStatisticsFact> stats, String departmentName) {
        if (stats.size() < 2) return new ArrayList<>();

        DepartmentStatisticsFact latest = stats.get(stats.size() - 1);
        DepartmentStatisticsFact previous = stats.get(stats.size() - 2);

        int staffChange = latest.getActiveStaffCount() - previous.getActiveStaffCount();
        int detaineeChange = latest.getDetaineesAssigned() - previous.getDetaineesAssigned();

        List<ReportInsight> insights = new ArrayList<>();

        if (staffChange != 0) {
            insights.add(new ReportInsight(
                    "Thay đổi cán bộ",
                    departmentName + " " + (staffChange > 0 ? "tăng" : "giảm") + " " + Math.abs(staffChange) + " cán bộ",
                    (staffChange > 0 ? "+" : "") + staffChange + " người"
            ));
        }

        if (detaineeChange != 0) {
            insights.add(new ReportInsight(
                    "Thay đổi phạm nhân",
                    "Số phạm nhân được giao " + (detaineeChange > 0 ? "tăng" : "giảm") + " " + Math.abs(detaineeChange),
                    (detaineeChange > 0 ? "+" : "") + detaineeChange + " người"
            ));
        }

        return insights;
    }

    private List<ReportInsight> createDepartmentComparisonInsights(List<Map<String, Object>> data) {
        if (data.isEmpty()) return new ArrayList<>();

        // Department with highest efficiency
        Map<String, Object> mostEfficient = data.stream()
                .max(Comparator.comparing(row -> (Double) row.get("efficiency")))
                .orElse(data.get(0));

        // Department with most staff
        Map<String, Object> largestDept = data.get(0); // Already sorted by activeStaffCount DESC

        return Arrays.asList(
                new ReportInsight(
                        "Phòng ban hiệu quả nhất",
                        "Phòng " + mostEfficient.get("department") + " có hiệu suất cao nhất",
                        String.format("%.2f phạm nhân/cán bộ", mostEfficient.get("efficiency"))
                ),
                new ReportInsight(
                        "Phòng ban lớn nhất",
                        "Phòng " + largestDept.get("department") + " có nhiều cán bộ hoạt động nhất",
                        largestDept.get("activeStaffCount") + " cán bộ"
                )
        );
    }

    private String getDepartmentName(Integer departmentId) {
        return departmentRepository.findById(departmentId)
                .map(DepartmentEntity::getName)
                .orElse("Unknown Department");
    }
    protected ReportResponse createEmptyReport(String title) {
        return new ReportResponseBuilder(title)
                .columns(new ArrayList<>())
                .data(new ArrayList<>())
                .dataSource("REAL_TIME")
                .build();
    }
}
