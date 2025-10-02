package vn.mk.eid.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_statistics_fact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatisticsFactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private Integer month;

    // Aggregated metrics
    @Column(name = "total_detainees")
    private Long totalDetainees = 0L;

    @Column(name = "new_detainees")
    private Long newDetainees = 0L;

    @Column(name = "released_detainees")
    private Long releasedDetainees = 0L;

    @Column(name = "total_staff")
    private Long totalStaff = 0L;

    @Column(name = "new_staff")
    private Long newStaff = 0L;

    @Column(name = "new_identity_records")
    private Long newIdentityRecords = 0L;

    @Column(name = "new_fingerprint_cards")
    private Long newFingerprintCards = 0L;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;
}
