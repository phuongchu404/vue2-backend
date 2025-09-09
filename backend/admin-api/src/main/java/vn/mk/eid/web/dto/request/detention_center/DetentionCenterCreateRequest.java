package vn.mk.eid.web.dto.request.detention_center;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import vn.mk.eid.common.constant.ExceptionConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class DetentionCenterCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String code;

    private String address;

    private String wardId;

    private String provinceId;

    private String phone;

    @Email(message = ExceptionConstants.EMAIL_INVALID)
    private String email;

    private String director;

    private String deputyDirector;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishedDate;

    @Min(1)
    private Integer capacity;

    @Min(0)
    private Integer currentPopulation;
}
