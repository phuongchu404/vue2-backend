package vn.mk.eid.web.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FingerprintImpressionResponse {
    private Long id;
    private Long fingerprintCardId;
    private String finger;
    private String kind;
    private String bucket;
    private String imageKey;
    private String objectUrl;
    private String linkUrl;
    private Short qualityScore;
    private LocalDateTime capturedAt;
    private Long size;
}
