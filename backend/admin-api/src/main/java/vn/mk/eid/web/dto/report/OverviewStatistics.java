package vn.mk.eid.web.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverviewStatistics implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long totalDetainees;
    private Long totalStaff;
    private Long totalIdentity;
    private Long totalFingerprint;
    private Long detaineeChange;
    private Long staffChange;
    private Long identityChange;
    private Long fingerprintChange;

    // Additional metadata
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime lastUpdated;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataSource;

    public OverviewStatistics(Long activeDetainees, Long activeStaff, Long totalIdentityRecords,
                              Long totalFingerprintCards, Long detaineeChange, Long staffChange,
                              Long identityChange, Long fingerprintChange) {
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
