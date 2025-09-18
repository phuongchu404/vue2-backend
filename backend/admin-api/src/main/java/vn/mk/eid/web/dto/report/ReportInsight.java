package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportInsight {
    private String title;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String value;      // Highlighted value

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trend;      // "UP", "DOWN", "STABLE"

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String severity;   // "INFO", "WARNING", "SUCCESS", "ERROR"

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;

    public ReportInsight(String title, String description, String value) {
        this.title = title;
        this.description = description;
        this.value = value;
    }
}
