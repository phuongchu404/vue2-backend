package vn.mk.eid.common.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AuditRequest {
    String event;
    String message;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date from;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date to;
}
