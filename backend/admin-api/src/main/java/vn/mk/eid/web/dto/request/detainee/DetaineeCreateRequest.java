package vn.mk.eid.web.dto.request.detainee;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class DetaineeCreateRequest {
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date detentionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedReleaseDate;

    private String caseNumber;

    private String charges;

    private String sentenceDuration;

    private String courtName;

    private Integer detentionCenterId;

    private String cellNumber;

    private String notes;
}
