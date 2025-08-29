package vn.mk.eid.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PhotoUploadRequest {
    @NotNull(message = "Identity record ID is required")
    private Long identityRecordId;

    @NotBlank(message = "View is required")
    private String view; //'FRONT', 'LEFT_PROFILE', 'RIGHT_PROFILE'
}
