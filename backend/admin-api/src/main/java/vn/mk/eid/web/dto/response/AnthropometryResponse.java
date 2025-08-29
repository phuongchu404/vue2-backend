package vn.mk.eid.web.dto.response;

import lombok.Data;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO for {@link vn.mk.eid.common.dao.entity.AnthropometryEntity}
 */
@Data
public class AnthropometryResponse implements Serializable {
    @NotNull
    private Long identityRecordId;
    private String faceShape;
    private Float heightCm;
    private String noseBridge;
    private String distinctiveMarks;
    private String earLowerFold;
    private String earLobe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}