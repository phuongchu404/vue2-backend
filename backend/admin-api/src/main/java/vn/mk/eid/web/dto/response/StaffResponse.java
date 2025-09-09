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
    private String gender;

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
    private String permanentWardFullName;
    private String permanentProvinceId;
    private String permanentProvinceFullName;

    private String temporaryAddress;
    private String temporaryWardId;
    private String temporaryWardFullName;
    private String temporaryProvinceId;
    private String temporaryProvinceFullName;

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
}
