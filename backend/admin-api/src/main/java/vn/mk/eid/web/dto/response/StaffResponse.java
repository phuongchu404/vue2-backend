package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import vn.mk.eid.web.constant.Gender;
import vn.mk.eid.web.utils.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class StaffResponse {
    private Integer id;
    private String staffCode;
    private String profileNumber;
    private String fullName;
    private Integer gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String genderText;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String placeOfBirth;
    private String idNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idIssueDate;

    private String idIssuePlace;

    private Integer ethnicityId;
    private String ethnicityName;

    private Integer religionId;
    private String religionName;

    private String permanentAddress;
    private String permanentWardId;
    private String permanentProvinceId;

    private String temporaryAddress;
    private String temporaryWardId;
    private String temporaryProvinceId;

    private String phone;
    private String email;
    private String emergencyContact;
    private String emergencyPhone;

    private Integer detentionCenterId;
    private String detentionCenterName;
    private String detentionCenterCode;

    private Integer departmentId;
    private String departmentName;
    private String departmentCode;

    private Integer positionId;
    private String positionName;
    private String rank;

    private Integer educationLevelId;
    private String educationLevelName;
    private String status;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public StaffResponse(Integer id, String staffCode, String profileNumber, String fullName, String genderText, LocalDate dateOfBirth, String placeOfBirth, String idNumber, String ethnicityName, String religionName, String phone, String email, String departmentName, String positionName, String rank, String educationLevelName, String status, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.staffCode = staffCode;
        this.profileNumber = profileNumber;
        this.fullName = fullName;
        this.genderText = genderText;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.idNumber = idNumber;
        this.ethnicityName = ethnicityName;
        this.religionName = religionName;
        this.phone = phone;
        this.email = email;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.rank = rank;
        this.educationLevelName = educationLevelName;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getGender() {
        if (gender == null && StringUtil.isNotBlank(this.genderText)) {
            Integer genderId = Gender.getIdByCode(this.genderText);
            this.genderText = null;
            return genderId;
        }
        return gender;
    }
}
