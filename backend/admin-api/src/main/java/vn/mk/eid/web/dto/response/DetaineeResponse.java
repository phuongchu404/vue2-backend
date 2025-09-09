package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import vn.mk.eid.web.constant.Gender;
import vn.mk.eid.web.utils.StringUtil;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class DetaineeResponse {
    private Long id;
    private String detaineeCode;
    private String profileNumber;
    private String fullName;
    private String aliasName;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private String placeOfBirth;
    private String idNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date idIssueDate;
    private String idIssuePlace;

    private Integer nationalityId;
    private Integer ethnicityId;
    private Integer religionId;
    private String nationalityName;
    private String ethnicityName;
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

    private String currentAddress;
    private String currentWardId;
    private String currentWardFullName;
    private String currentProvinceFullName;
    private String currentProvinceId;

    private String occupation;
    private String fatherName;
    private String motherName;
    private String spouseName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date detentionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedReleaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualReleaseDate;

    private String caseNumber;
    private String charges;
    private String sentenceDuration;
    private String courtName;

    private String detentionCenterName;
    private String detentionCenterCode;
    private Integer detentionCenterId;
    private String cellNumber;
    private String status;
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String genderText;
//
//    public Integer getGender() {
//        if (gender == null && StringUtil.isNotBlank(genderText)) {
//            gender = Gender.getIdByCode(genderText);
//            genderText = null;
//        }
//        return gender;
//    }
}
