package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class FingerprintCardResponse {
    private Long id;

    private Long personId;

    private String personName;

    private String personCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private String createdPlace;

    private String dp;

    private String tw;

    private String fpFormula;

    private String reasonNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
