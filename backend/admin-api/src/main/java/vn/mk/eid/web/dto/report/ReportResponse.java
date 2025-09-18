package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private String title;
    private List<ReportColumn> columns;
    private List<Map<String, Object>> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> summary;    // Summary row data

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ChartData chartData;            // Chart visualization

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ReportInsight> insights;   // AI-generated insights

    // Metadata
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime generatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalRecords;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer currentPage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataSource; // "REAL_TIME", "CACHED", "PRE_AGGREGATED"

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long executionTimeMs;

    // Convenience constructors
    public ReportResponse(String title, List<ReportColumn> columns, List<Map<String, Object>> data) {
        this.title = title;
        this.columns = columns;
        this.data = data;
        this.generatedAt = LocalDateTime.now();
        this.totalRecords = data != null ? data.size() : 0;
    }

    public ReportResponse(String s, List<ReportColumn> columns, List<Map<String, Object>> data, ChartData chartData, List<ReportInsight> insights) {
        this.title = s;
        this.columns = columns;
        this.data = data;
        this.chartData = chartData;
        this.insights = insights;
        this.generatedAt = LocalDateTime.now();
        this.totalRecords = data != null ? data.size() : 0;
    }

    public ReportResponse(String s, List<ReportColumn> columns, List<Map<String, Object>> data, List<ReportInsight> forecastInsights) {
        this.title = s;
        this.columns = columns;
        this.data = data;
        this.insights = forecastInsights;
        this.generatedAt = LocalDateTime.now();
        this.totalRecords = data != null ? data.size() : 0;
    }

    public ReportResponse(String s, List<ReportColumn> columns, List<Map<String, Object>> data, Map<String, Object> summary, ChartData chartData, List<ReportInsight> insights) {
        this.title = s;
        this.columns = columns;
        this.data = data;
        this.summary = summary;
        this.chartData = chartData;
        this.insights = insights;
        this.generatedAt = LocalDateTime.now();
        this.totalRecords = data != null ? data.size() : 0;
    }
}
