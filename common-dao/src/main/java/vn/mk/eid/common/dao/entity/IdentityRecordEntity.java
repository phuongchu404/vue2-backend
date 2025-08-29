package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang danh ban
@Getter
@Setter
@Entity
@Table(name = "identity_record")
public class IdentityRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "detainee_id", nullable = false)
    private Long detaineeId; // id nguoi bi tam giu

    @Column(name = "created_place")
    private String createdPlace; // Noi lap

    @Column(name = "reason_note")
    private String reasonNote; // Ly do lap

    @Column(name = "arrest_date")
    private LocalDate arrestDate; // Ngay bat

    @Column(name = "arrest_unit")
    private String arrestUnit; // Don vi bat

    @Column(name = "fp_classification")
    private String fpClassification; // C/T van tay

    @Column(name = "dp")
    private String dp;

    @Column(name = "tw")
    private String tw;

    @Column(name = "ak_file_no")
    private String akFileNo; // So ho so AK

    @Column(name = "notes")
    private String notes; // Ghi chu

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}