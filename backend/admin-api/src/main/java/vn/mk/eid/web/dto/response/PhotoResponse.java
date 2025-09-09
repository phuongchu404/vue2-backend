package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PhotoResponse {
    private Long id;
    private Long identityRecordId;
    private String view;
    private String bucket;
    private String objectKey;
    private String objectUrl;
    private String mimeType;
    private Long sizeBytes;
    private String linkUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String finger;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
