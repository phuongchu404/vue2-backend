package vn.mk.eid.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.web.constant.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class StaffUpdateRequest extends StaffCreateRequest {
    private String status;
    private Boolean isActive;
}
