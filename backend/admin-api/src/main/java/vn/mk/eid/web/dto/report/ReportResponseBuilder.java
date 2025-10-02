package vn.mk.eid.web.dto.report;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportResponseBuilder implements Serializable {
    private static final long serialVersionUID = 1L;
    private ReportResponse response;

    public ReportResponseBuilder(String title) {
        this.response = new ReportResponse();
        this.response.setTitle(title);
        this.response.setGeneratedAt(LocalDateTime.now());
    }

    public ReportResponseBuilder columns(List<ReportColumn> columns) {
        this.response.setColumns(columns);
        return this;
    }

    public ReportResponseBuilder data(List<Map<String, Object>> data) {
        this.response.setData(data);
        this.response.setTotalRecords(data != null ? data.size() : 0);
        return this;
    }

    public ReportResponseBuilder summary(Map<String, Object> summary) {
        this.response.setSummary(summary);
        return this;
    }

    public ReportResponseBuilder chartData(ChartData chartData) {
        this.response.setChartData(chartData);
        return this;
    }

    public ReportResponseBuilder insights(List<ReportInsight> insights) {
        this.response.setInsights(insights);
        return this;
    }

    public ReportResponseBuilder dataSource(String dataSource) {
        this.response.setDataSource(dataSource);
        return this;
    }

    public ReportResponseBuilder executionTime(Long executionTimeMs) {
        this.response.setExecutionTimeMs(executionTimeMs);
        return this;
    }

    public ReportResponse build() {
        return this.response;
    }
}
