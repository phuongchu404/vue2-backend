package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportResponse {
    private String fileName;
    private String downloadUrl;
    private String format;
    private Long fileSizeBytes;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
}
