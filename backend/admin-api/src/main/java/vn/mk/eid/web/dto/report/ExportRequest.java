package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequest {
    private String reportType;
    private String format; // "PDF", "EXCEL", "CSV"
    private LocalDate fromDate;
    private LocalDate toDate;
    private Map<String, Object> parameters;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String template; // Export template name

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean includeCharts;
}
