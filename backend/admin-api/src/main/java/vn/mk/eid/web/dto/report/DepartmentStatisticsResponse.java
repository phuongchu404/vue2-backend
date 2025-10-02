package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentStatisticsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer departmentId;
    private String departmentName;
    private LocalDate reportDate;
    private Integer staffCount;
    private Integer activeStaffCount;
    private Integer detaineesAssigned;
    private Double efficiency; // detainees per active staff
    private String trend; // "UP", "DOWN", "STABLE"
}
