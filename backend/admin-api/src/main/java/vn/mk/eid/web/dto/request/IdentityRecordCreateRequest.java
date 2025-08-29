package vn.mk.eid.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdentityRecordCreateRequest extends IdentityRecordUpdateRequest {
    @NotNull
    private String detaineeCode;
}
