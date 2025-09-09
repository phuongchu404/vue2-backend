package vn.mk.eid.web.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.constant.ExceptionConstants;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class FingerprintCardCreateRequest {
    @NotNull(message = ExceptionConstants.DETAINEE_CODE_NOT_NULL)
    private String detaineeCode;

//    @NotNull(message = "Created date is required")
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate createdDate;

    private String createdPlace;
    private String dp;
    private String tw;
    private String fpFormula;
    private String reasonNote;

    private Map<String, MultipartFile> fingerprintImages;
}
