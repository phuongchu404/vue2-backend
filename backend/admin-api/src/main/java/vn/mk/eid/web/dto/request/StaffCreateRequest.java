package vn.mk.eid.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.web.constant.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class StaffCreateRequest {
    private String profileNumber;

    @NotBlank(message = ExceptionConstants.STAFF_NAME_NOT_NULL)
    private String fullName;

    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String placeOfBirth;

    @Size(max = 20, message = ExceptionConstants.IDENTITY_INVALID)
    private String idNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idIssueDate;
    @Size(max = 100, message = ExceptionConstants.ISSUE_PLACE_INVALID)
    private String idIssuePlace;

    private Integer ethnicityId;

    private Integer religionId;

    private String permanentAddress;

    private String permanentWardId;

    private String permanentProvinceId;

    private String temporaryAddress;

    private String temporaryWardId;

    private String temporaryProvinceId;

    private String phone;

    @Email(message = ExceptionConstants.EMAIL_INVALID)
    private String email;

    private String emergencyContact;

    private String emergencyPhone;

    @NotNull(message = ExceptionConstants.DETENTION_CENTER_NOT_NULL)
    private Integer detentionCenterId;

    private Integer departmentId;

    private Integer positionId;

    private String rank;

    private Integer educationLevelId;
}
