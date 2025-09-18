package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartData {
    private String type;
    private Map<String, Object> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> options;

    public ChartData(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }
}
