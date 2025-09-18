package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverviewStatistics {
    private Integer totalDetainees;
    private Integer totalStaff;
    private Integer totalIdentity;
    private Integer totalFingerprint;
    private Integer detaineeChange;
    private Integer staffChange;
    private Integer identityChange;
    private Integer fingerprintChange;

    // Additional metadata
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime lastUpdated;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataSource;

    public OverviewStatistics(Integer activeDetainees, Integer activeStaff, Integer totalIdentityRecords,
                              Integer totalFingerprintCards, Integer detaineeChange, Integer staffChange,
                              Integer identityChange, Integer fingerprintChange) {
        this.totalDetainees = activeDetainees;
        this.totalStaff = activeStaff;
        this.totalIdentity = totalIdentityRecords;
        this.totalFingerprint = totalFingerprintCards;
        this.detaineeChange = detaineeChange;
        this.staffChange = staffChange;
        this.identityChange = identityChange;
        this.fingerprintChange = fingerprintChange;
        this.lastUpdated = LocalDateTime.now();
        this.dataSource = "Internal Database";

    }
}
