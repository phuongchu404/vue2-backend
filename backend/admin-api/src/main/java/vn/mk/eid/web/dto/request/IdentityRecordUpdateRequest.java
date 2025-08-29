package vn.mk.eid.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class IdentityRecordUpdateRequest {
    // IdentityRecord
    private String createdPlace;
    private String reasonNote;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrestDate;
    private String arrestUnit;
    private String fpClassification;
    private String dp;
    private String tw;
    private String akFileNo;
    private String notes;

    // Anthropometry
    private String faceShape;
    private Float heightCm;
    private String noseBridge;
    private String distinctiveMarks;
    private String earLowerFold;
    private String earLobe;
}
