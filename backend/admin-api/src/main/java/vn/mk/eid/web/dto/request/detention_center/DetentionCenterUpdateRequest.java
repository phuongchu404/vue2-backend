package vn.mk.eid.web.dto.request.detention_center;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class DetentionCenterUpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    private String address;

    private String wardId;

    private String provinceId;

    private String phone;

    @Email
    private String email;

    private String director;

    private String deputyDirector;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishedDate;

    private Integer capacity;

    private Integer currentPopulation;

    private Boolean isActive = Boolean.TRUE;
}
