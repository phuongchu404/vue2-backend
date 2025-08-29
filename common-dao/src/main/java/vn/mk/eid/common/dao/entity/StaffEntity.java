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
// bang danh sach can bo, nhan vien trai giam
@Getter
@Setter
@Entity
@Table(name = "staff")
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "staff_code", nullable = false, length = 20)
    private String staffCode; // mã cán bộ, nhân viên

    @Column(name = "profile_number", length = 50)
    private String profileNumber; // số hồ sơ cán bộ, nhân viên

    @Size(max = 255)
    @javax.validation.constraints.NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName; // họ và tên đầy đủ

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth")
    @Type(type = "org.hibernate.type.TextType")
    private String placeOfBirth;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "id_number", length = 20)
    private String idNumber; // CMND/CCCD/Hộ chiếu số

    @Column(name = "id_issue_date")
    private LocalDate idIssueDate; // ngày cấp CMND/CCCD/Hộ chiếu

    @javax.validation.constraints.Size(max = 255)
    @Column(name = "id_issue_place")
    private String idIssuePlace; // nơi cấp CMND/CCCD/Hộ chiếu

    @Column(name = "ethnicity_id")
    private Integer ethnicityId; // mã dân tộc

    @Column(name = "religion_id")
    private Integer religionId; // mã tôn giáo

    // địa chỉ thường trú
    @Column(name = "permanent_address")
    @Type(type = "org.hibernate.type.TextType")
    private String permanentAddress;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "permanent_ward_id", length = 20)
    private String permanentWardId;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "permanent_province_id", length = 20)
    private String permanentProvinceId;

    // địa chỉ tạm trú
    @Column(name = "temporary_address")
    @Type(type = "org.hibernate.type.TextType")
    private String temporaryAddress;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "temporary_ward_id", length = 20)
    private String temporaryWardId;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "temporary_province_id", length = 20)
    private String temporaryProvinceId;

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @javax.validation.constraints.Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @javax.validation.constraints.Size(max = 255)
    @Column(name = "emergency_contact")
    private String emergencyContact; // người liên hệ khẩn cấp

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone; // số điện thoại liên hệ khẩn cấp

    @Column(name = "detention_center_id")
    private Integer detentionCenterId; // mã trai giam

    @Column(name = "department_id")
    private Integer departmentId; // mã phòng ban

    @Column(name = "position_id")
    private Integer positionId; // mã chức vụ

    @javax.validation.constraints.Size(max = 50)
    @Column(name = "rank", length = 50)
    private String rank; // cấp bậc

    @Column(name = "education_level_id")
    private Integer educationLevelId; // mã trình độ học vấn

    @javax.validation.constraints.Size(max = 20)
    @Column(name = "status", length = 20)
    private String status; //ACTIVE, INACTIVE, RETIRED, TRANSFERRED

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}