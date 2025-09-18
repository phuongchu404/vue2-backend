package vn.mk.eid.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_statistics_fact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatisticsFactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date", unique = true, nullable = false)
    private LocalDate reportDate;

    // Detainee metrics
    @Column(name = "total_detainees")
    private Long totalDetainees = 0L;

    @Column(name = "new_detainees")
    private Integer newDetainees = 0;

    @Column(name = "released_detainees")
    private Long releasedDetainees = 0L;

    @Column(name = "active_detainees")
    private Long activeDetainees = 0L;

    // Staff metrics
    @Column(name = "total_staff")
    private Long totalStaff = 0L;

    @Column(name = "new_staff")
    private Long newStaff = 0L;

    @Column(name = "active_staff")
    private Long activeStaff = 0L;

    // Identity records metrics
    @Column(name = "total_identity_records")
    private Long totalIdentityRecords = 0L;

    @Column(name = "new_identity_records")
    private Long newIdentityRecords = 0L;

    // Fingerprint cards metrics
    @Column(name = "total_fingerprint_cards")
    private Long totalFingerprintCards = 0L;

    @Column(name = "new_fingerprint_cards")
    private Long newFingerprintCards = 0L;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
