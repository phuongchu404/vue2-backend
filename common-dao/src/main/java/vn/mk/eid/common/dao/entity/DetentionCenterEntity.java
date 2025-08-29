package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang thong tin trung tam giam giu
@Getter
@Setter
@Entity
@Table(name = "detention_centers")
public class DetentionCenterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "address")
    private String address;

    @Column(name = "ward_id", length = 20)
    private String wardId;

    @Column(name = "province_id", length = 20)
    private String provinceId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "director", length = 100)
    private String director; // giam doc trai giam

    @Column(name = "deputy_director", length = 100)
    private String deputyDirector; // pho giam doc trai giam

    @Column(name = "established_date")
    private LocalDate establishedDate; // ngay thanh lap

    @Column(name = "capacity")
    private Integer capacity; // suc chua toi da

    @Column(name = "current_population")
    private Integer currentPopulation; // so tu nhan hien co

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}