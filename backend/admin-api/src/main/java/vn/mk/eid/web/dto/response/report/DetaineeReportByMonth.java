package vn.mk.eid.web.dto.response.report;

import lombok.Data;

@Data
public class DetaineeReportByMonth {
    private String month;
    private Integer newDetainees = 0;
    private Integer released = 0;
    private Integer total = 0;
}
