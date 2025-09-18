package vn.mk.eid.web.dto.request.report;

import lombok.Data;

@Data
public class DetaineeReportStatus {
    private Integer detentionCenterId;
    private String fromDate;
    private String toDate;
}
