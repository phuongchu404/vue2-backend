package vn.mk.eid.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class DetentionCenterResponse {
    private Integer id;
    private String name;
    private String code;
    private String address;
    private String wardId;
    private String provinceId;
    private String phone;
    private String email;
    private String director;
    private String deputyDirector;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishedDate;

    private Integer capacity;
    private Integer currentPopulation;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
