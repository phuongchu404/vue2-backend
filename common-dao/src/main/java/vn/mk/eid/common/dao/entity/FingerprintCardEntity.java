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

/**
 * @author mk
 * @date 06-Aug-2025
 */
// Bang chi ban
@Getter
@Setter
@Entity
@Table(name = "fingerprint_card")
public class FingerprintCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @javax.validation.constraints.NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate; // Ngay lap

    @Column(name = "created_place")
    private String createdPlace; // Noi lap

    @Column(name = "dp")
    private String dp;

    @Column(name = "tw")
    @Type(type = "org.hibernate.type.TextType")
    private String tw;

    @Column(name = "fp_formula")
    private String fpFormula; // C/T van tay

    @Column(name = "reason_note")
    private String reasonNote; // Ly do lap

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}