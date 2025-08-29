package vn.mk.eid.common.dao.entity;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// bang thong tin tu nhan
@Data
@Entity
@Table(name = "detainees")
@EntityListeners(value = {AuditLogListener.class})
public class DetaineeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "detainee_code", nullable = false, length = 20)
    private String detaineeCode; // ma tu nhan


    @Column(name = "profile_number", length = 50)
    private String profileNumber; // so ho so tu nhan

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;


    @Column(name = "alias_name")
    private String aliasName; // ten goi khac

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth; // noi sinh


    @Column(name = "id_number", length = 20)
    private String idNumber; // CMND/CCCD/Ho chieu

    @Column(name = "id_issue_date")
    private Date idIssueDate; // ngay cap CMND/CCCD/ho chieu

    @javax.validation.constraints.Size(max = 255)
    @Column(name = "id_issue_place")
    private String idIssuePlace; // noi cap CMND/CCCD/ho chieu

    @Column(name = "nationality_id")
    private Integer nationalityId; // ma quoc tich

    @Column(name = "ethnicity_id")
    private Integer ethnicityId; // ma dan toc

    @Column(name = "religion_id")
    private Integer religionId; // ma ton giao

    // dia chi thuong tru
    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "permanent_ward_id", length = 20)
    private String permanentWardId;

    @Column(name = "permanent_province_id", length = 20)
    private String permanentProvinceId;

    // dia chi tam tru
    @Column(name = "temporary_address")
    private String temporaryAddress;

    @Column(name = "temporary_ward_id", length = 20)
    private String temporaryWardId;

    @Column(name = "temporary_province_id", length = 20)
    private String temporaryProvinceId;

    // dia chi hien tai
    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "current_ward_id", length = 20)
    private String currentWardId;

    @Column(name = "current_province_id", length = 20)
    private String currentProvinceId;

    @Column(name = "occupation")
    private String occupation; // nghe nghiep

    @Column(name = "father_name")
    private String fatherName; // ten cha

    @Column(name = "mother_name")
    private String motherName; // ten me

    @Column(name = "spouse_name")
    private String spouseName; // ten vo/chong

    // thong tin phap ly
    @NotNull
    @Column(name = "detention_date", nullable = false)
    private Date detentionDate; // ngay tam giam

    @Column(name = "expected_release_date")
    private Date expectedReleaseDate; // ngay du kien tha

    @Column(name = "actual_release_date")
    private Date actualReleaseDate; // ngay thuc te tha

    @Size(max = 50)
    @Column(name = "case_number", length = 50)
    private String caseNumber; // so vu an

    @Column(name = "charges")
    private String charges; // toi danh

    @Column(name = "sentence_duration", length = 50)
    private String sentenceDuration;  // thoi han tu

    @Column(name = "court_name")
    private String courtName; // ten toa an xet xu

    @Column(name = "detention_center_id")
    private Integer detentionCenterId; // ma trai giam

    @Column(name = "cell_number", length = 20)
    private String cellNumber; // ma so buong giam

    @Column(name = "status", length = 20)
    private String status; // trang thai tu nhan (DETAINED-dang giam, RELEASED-da tha, TRANSFERRED-da chuyen trai, DECEASED-da chet)

    @Column(name = "notes")
    private String notes; // ghi chú

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @Override
//    public String getMessage() {
//        return "Tù nhân [ CMND/CCCD/Hộ chiếu số = " + idNumber + ", Họ và tên = " + fullName + "]";
//    }
//
//    @Override
//    public Integer getUserIdAudit() {
//        return 0;
//    }
}
