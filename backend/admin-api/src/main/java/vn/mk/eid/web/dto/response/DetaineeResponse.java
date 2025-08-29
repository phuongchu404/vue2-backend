package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

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

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;

    private String placeOfBirth;
    private String idNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date idIssueDate;

    private String idIssuePlace;
    private Integer nationalityId;
    private Integer ethnicityId;
    private Integer religionId;

    private String permanentAddress;
    private String permanentWardId;
    private String permanentProvinceId;

    private String temporaryAddress;
    private String temporaryWardId;
    private String temporaryProvinceId;

    private String currentAddress;
    private String currentWardId;
    private String currentProvinceId;

    private String occupation;
    private String fatherName;
    private String motherName;
    private String spouseName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date detentionDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date expectedReleaseDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date actualReleaseDate;

    private String caseNumber;
    private String charges;
    private String sentenceDuration;
    private String courtName;

//    private String detentionCenterName;
//    private String detentionCenterCode;
    private Integer detentionCenterId;
    private String cellNumber;
    private String status;
    private String notes;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}
