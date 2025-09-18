package vn.mk.eid.web.dto.response.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetaineeReportByStatus {
    private String type;
    private Integer count;
    private BigDecimal percent = BigDecimal.ZERO;
}
