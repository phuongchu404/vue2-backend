package vn.mk.eid.web.dto.response.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DetaineeReportByStatusResponse {
    private Integer count = 0;
    private BigDecimal percent = BigDecimal.valueOf(100);
    private List<DetaineeReportByStatus> details;
}
