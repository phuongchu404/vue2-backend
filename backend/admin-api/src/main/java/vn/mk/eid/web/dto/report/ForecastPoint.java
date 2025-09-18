package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastPoint {
    private LocalDate date;
    private Integer predictedStaffNeed;
    private Integer predictedWorkload;
    private Double confidence;
}
