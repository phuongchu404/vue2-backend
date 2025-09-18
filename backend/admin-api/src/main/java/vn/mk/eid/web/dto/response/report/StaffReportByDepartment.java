package vn.mk.eid.web.dto.response.report;

import lombok.Data;

@Data
public class StaffReportByDepartment {
    private String departmentName;
    private String detentionCenterName;
    private Integer count;
    private Integer active;
}
