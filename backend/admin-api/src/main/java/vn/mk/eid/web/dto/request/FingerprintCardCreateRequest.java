package vn.mk.eid.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Data
public class FingerprintCardCreateRequest {
    @NotNull(message = "Person ID is required")
    private Long detaineeId;

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
