package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentTrendsRequest {
    private Long departmentId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String aggregationType; // "DAILY", "WEEKLY", "MONTHLY"
}
