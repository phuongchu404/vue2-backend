package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/*
    * @author mk
    * @date 06-Aug-2025
    */
// dac diem hinh thai hoc trong danh ban
@Getter
@Setter
@Entity
@Table(name = "anthropometry")
public class AnthropometryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "identity_record_id", nullable = false)
    private Long identityRecordId; // id danh ban

    @Column(name = "face_shape")
    private String faceShape; //khuon mat

    @Column(name = "height_cm")
    private Float heightCm; // chieu cao

    @Column(name = "nose_bridge")
//    @Type(type = "org.hibernate.type.TextType")
    private String noseBridge; // song mui

    @Column(name = "distinctive_marks")
    private String distinctiveMarks; // dau vet rieng

    @Column(name = "ear_lower_fold")
    private String earLowerFold; // nep tai duoi

    @Column(name = "ear_lobe")
    private String earLobe; // dai tai

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}