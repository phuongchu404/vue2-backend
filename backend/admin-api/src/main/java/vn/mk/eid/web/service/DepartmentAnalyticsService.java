package vn.mk.eid.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.DepartmentEntity;
import vn.mk.eid.common.dao.entity.DepartmentStatisticsFactEntity;
import vn.mk.eid.common.dao.repository.DepartmentRepository;
import vn.mk.eid.common.dao.repository.DepartmentStatisticsFactRepository;
import vn.mk.eid.web.dto.report.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentAnalyticsService {
    private final DepartmentStatisticsFactRepository departmentStatsRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Phân tích hiệu suất các phòng ban
     */
    public List<DepartmentStatisticsResponse> analyzeDepartmentPerformance(LocalDate fromDate, LocalDate toDate) {
        List<Object[]> results = departmentStatsRepository.findTopPerformingDepartments(fromDate, toDate, 10);

        return results.stream()
                .map(row -> {
                    DepartmentStatisticsResponse response = new DepartmentStatisticsResponse();
                    response.setDepartmentId((Integer) row[0]);
                    response.setDepartmentName((String) row[1]);
                    response.setEfficiency(((Number) row[2]).doubleValue());
                    response.setTrend(calculateTrend((Integer) row[0], fromDate, toDate));
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Phân tích phân bố khối lượng công việc
     */
    public ReportResponse getWorkloadDistributionReport(LocalDate fromDate, LocalDate toDate) {
        List<Object[]> results = departmentStatsRepository.getDepartmentWorkloadDistribution(fromDate, toDate);

        List<ReportColumn> columns = Arrays.asList(
                new ReportColumn("department", "Phòng Ban", "text"),
                new ReportColumn("totalDetainees", "Tổng phạm nhân", "number"),
                new ReportColumn("avgStaff", "TB cán bộ", "number"),
                new ReportColumn("workloadRatio", "Tỷ lệ tải", "number")
        );

        List<Map<String, Object>> data = results.stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("department", row[1]);
                    item.put("totalDetainees", ((Number) row[2]).intValue());
                    item.put("avgStaff", Math.round(((Number) row[3]).doubleValue()));

                    double avgStaff = ((Number) row[3]).doubleValue();
                    double workloadRatio = avgStaff > 0 ? ((Number) row[2]).doubleValue() / avgStaff : 0;
                    item.put("workloadRatio", Math.round(workloadRatio * 100.0) / 100.0);

                    return item;
                })
                .collect(Collectors.toList());

        // Create workload chart
        ChartData chartData = createWorkloadChart(data);

        // Create insights
        List<ReportInsight> insights = createWorkloadInsights(data);

        return new ReportResponse(
                "Báo Cáo Phân Bố Khối Lượng Công Việc",
                columns, data, chartData, insights
        );
    }

    /**
     * Dự báo nhu cầu nhân lực cho phòng ban
     */
    public ReportResponse getStaffingForecastReport(Integer departmentId, int forecastDays) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // Analyze last 30 days for forecasting

        List<DepartmentStatisticsFactEntity> historicalData = departmentStatsRepository
                .findByDepartmentIdAndDateRange(departmentId, startDate, endDate);

        if (historicalData.size() < 7) {
            return createEmptyReport("Không đủ dữ liệu lịch sử để dự báo");
        }

        // Simple linear trend analysis
        List<ForecastPoint> forecast = calculateStaffingForecast(historicalData, forecastDays);

        List<ReportColumn> columns = Arrays.asList(
                new ReportColumn("date", "Ngày", "date"),
                new ReportColumn("predictedStaffNeed", "Dự báo nhu cầu cán bộ", "number"),
                new ReportColumn("predictedWorkload", "Dự báo khối lượng", "number"),
                new ReportColumn("confidence", "Độ tin cậy (%)", "number")
        );

        List<Map<String, Object>> data = forecast.stream()
                .map(point -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", point.getDate().toString());
                    item.put("predictedStaffNeed", point.getPredictedStaffNeed());
                    item.put("predictedWorkload", point.getPredictedWorkload());
                    item.put("confidence", Math.round(point.getConfidence() * 100));
                    return item;
                })
                .collect(Collectors.toList());

        String departmentName = getDepartmentName(departmentId);

        return new ReportResponse(
                "Dự Báo Nhu Cầu Nhân Lực - " + departmentName,
                columns, data, createForecastInsights(forecast, departmentName)
        );
    }

    private String calculateTrend(Integer departmentId, LocalDate fromDate, LocalDate toDate) {
        List<DepartmentStatisticsFactEntity> stats = departmentStatsRepository
                .findByDepartmentIdAndDateRange(departmentId, fromDate, toDate);

        if (stats.size() < 2) return "STABLE";

        DepartmentStatisticsFactEntity first = stats.get(0);
        DepartmentStatisticsFactEntity last = stats.get(stats.size() - 1);

        double firstEfficiency = first.getActiveStaffCount() > 0
                ? (double) first.getDetaineesAssigned() / first.getActiveStaffCount()
                : 0;
        double lastEfficiency = last.getActiveStaffCount() > 0
                ? (double) last.getDetaineesAssigned() / last.getActiveStaffCount()
                : 0;

        if (lastEfficiency > firstEfficiency * 1.05) return "UP";
        if (lastEfficiency < firstEfficiency * 0.95) return "DOWN";
        return "STABLE";
    }

    private ChartData createWorkloadChart(List<Map<String, Object>> data) {
        Map<String, Object> chartDataMap = new HashMap<>();

        List<String> labels = data.stream()
                .map(row -> (String) row.get("department"))
                .collect(Collectors.toList());

        List<Double> workloadRatios = data.stream()
                .map(row -> (Double) row.get("workloadRatio"))
                .collect(Collectors.toList());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Tỷ lệ tải công việc");
        dataset.put("data", workloadRatios);
        dataset.put("backgroundColor", "#667eea");

        chartDataMap.put("labels", labels);
        chartDataMap.put("datasets", Arrays.asList(dataset));

        return new ChartData("bar", chartDataMap);
    }

    private List<ReportInsight> createWorkloadInsights(List<Map<String, Object>> data) {
        if (data.isEmpty()) return new ArrayList<>();

        // Find overloaded and underloaded departments
        double avgWorkload = data.stream()
                .mapToDouble(row -> (Double) row.get("workloadRatio"))
                .average()
                .orElse(0.0);

        Map<String, Object> maxWorkload = data.stream()
                .max(Comparator.comparing(row -> (Double) row.get("workloadRatio")))
                .orElse(data.get(0));

        Map<String, Object> minWorkload = data.stream()
                .min(Comparator.comparing(row -> (Double) row.get("workloadRatio")))
                .orElse(data.get(0));

        return Arrays.asList(
                new ReportInsight(
                        "Tải công việc trung bình",
                        String.format("Tỷ lệ tải trung bình là %.2f phạm nhân/cán bộ", avgWorkload),
                        String.format("%.2f", avgWorkload)
                ),
                new ReportInsight(
                        "Phòng ban tải cao nhất",
                        "Phòng " + maxWorkload.get("department") + " có tải công việc cao nhất",
                        String.format("%.2f", maxWorkload.get("workloadRatio"))
                ),
                new ReportInsight(
                        "Phòng ban tải thấp nhất",
                        "Phòng " + minWorkload.get("department") + " có tải công việc thấp nhất",
                        String.format("%.2f", minWorkload.get("workloadRatio"))
                )
        );
    }

    private List<ForecastPoint> calculateStaffingForecast(List<DepartmentStatisticsFactEntity> historicalData, int forecastDays) {
        // Simple linear regression for demonstration
        // In real implementation, use more sophisticated forecasting algorithms

        List<ForecastPoint> forecast = new ArrayList<>();
        LocalDate lastDate = historicalData.get(historicalData.size() - 1).getReportDate();

        // Calculate trend from historical data
        double avgStaffGrowth = calculateAverageGrowth(historicalData, DepartmentStatisticsFactEntity::getActiveStaffCount);
        double avgWorkloadGrowth = calculateAverageGrowth(historicalData, DepartmentStatisticsFactEntity::getDetaineesAssigned);

        DepartmentStatisticsFactEntity lastPoint = historicalData.get(historicalData.size() - 1);

        for (int i = 1; i <= forecastDays; i++) {
            LocalDate forecastDate = lastDate.plusDays(i);

            int predictedStaff = (int) Math.max(1, lastPoint.getActiveStaffCount() + (avgStaffGrowth * i));
            int predictedWorkload = (int) Math.max(0, lastPoint.getDetaineesAssigned() + (avgWorkloadGrowth * i));

            // Confidence decreases over time
            double confidence = Math.max(0.5, 1.0 - (i * 0.02)); // 2% decrease per day

            forecast.add(new ForecastPoint(forecastDate, predictedStaff, predictedWorkload, confidence));
        }

        return forecast;
    }

    private double calculateAverageGrowth(List<DepartmentStatisticsFactEntity> data, Function<DepartmentStatisticsFactEntity, Long> extractor) {
        if (data.size() < 2) return 0.0;

        List<Double> growthRates = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            long current = extractor.apply(data.get(i));
            long previous = extractor.apply(data.get(i - 1));

            if (previous != 0) {
                growthRates.add((double) (current - previous) / previous);
            }
        }

        return growthRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private List<ReportInsight> createForecastInsights(List<ForecastPoint> forecast, String departmentName) {
        if (forecast.isEmpty()) return new ArrayList<>();

        ForecastPoint firstPoint = forecast.get(0);
        ForecastPoint lastPoint = forecast.get(forecast.size() - 1);

        int staffChange = lastPoint.getPredictedStaffNeed() - firstPoint.getPredictedStaffNeed();
        int workloadChange = lastPoint.getPredictedWorkload() - firstPoint.getPredictedWorkload();

        return Arrays.asList(
                new ReportInsight(
                        "Xu hướng nhân lực",
                        departmentName + " dự báo " + (staffChange >= 0 ? "tăng" : "giảm") + " " + Math.abs(staffChange) + " cán bộ",
                        (staffChange >= 0 ? "+" : "") + staffChange + " người"
                ),
                new ReportInsight(
                        "Xu hướng khối lượng",
                        "Khối lượng công việc dự báo " + (workloadChange >= 0 ? "tăng" : "giảm") + " " + Math.abs(workloadChange),
                        (workloadChange >= 0 ? "+" : "") + workloadChange
                )
        );
    }

    private String getDepartmentName(Integer departmentId) {
        return departmentRepository.findById(departmentId)
                .map(DepartmentEntity::getName)
                .orElse("Unknown Department");
    }

    private ReportResponse createEmptyReport(String title) {
        return new ReportResponse(title, new ArrayList<>(), new ArrayList<>());
    }
}
