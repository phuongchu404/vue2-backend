package vn.mk.eid.web.dto.request.identity_record;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdentityRecordCreateRequest extends IdentityRecordUpdateRequest {
    @NotNull
    private String detaineeCode;
}
