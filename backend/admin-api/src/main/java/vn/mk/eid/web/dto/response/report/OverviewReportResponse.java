package vn.mk.eid.web.dto.response.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverviewReportResponse {
    private Integer totalDetentionCenter;
    private Integer totalDetainees;
    private Integer totalStaff;
    private Integer totalIdentity;
    private Integer totalFingerprint;
    private Integer detaineeChange;
    private Integer staffChange;
    private Integer identityChange;
    private Integer fingerprintChange;
}
