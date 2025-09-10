package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class IdentityRecordResponse {

    private Long id;
    private Long detaineeId;
    private String detaineeName;
    private String detaineeCode;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private AnthropometryResponse anthropometry; // Thong tin ve nhan dang hinh the

    private List<PhotoResponse> photos; // Anh chan dung
}
