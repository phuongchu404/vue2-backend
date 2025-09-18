package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportColumn {
    private String key;        // Field key for data access
    private String title;      // Display title
    private String type;       // "text", "number", "date", "percentage", "currency"

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean sortable;  // Can this column be sorted?

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String format;     // Format pattern for display

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer width;

    public ReportColumn(String key, String title, String type) {
        this.key = key;
        this.title = title;
        this.type = type;
    }
}
