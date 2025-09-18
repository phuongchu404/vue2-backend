package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private String type;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Map<String, Object> parameters;
}
