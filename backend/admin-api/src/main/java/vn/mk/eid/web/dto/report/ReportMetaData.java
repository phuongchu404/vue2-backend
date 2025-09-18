package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMetaData {
    private String reportId;
    private String reportName;
    private String description;
    private List<String> availableParameters;
    private LocalDateTime lastGenerated;
    private String category;
}
