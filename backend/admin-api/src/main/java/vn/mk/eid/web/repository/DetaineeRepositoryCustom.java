package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.dto.response.DetaineeResponse;
import vn.mk.eid.web.dto.response.report.DetaineeReportByMonth;
import vn.mk.eid.web.dto.response.report.DetaineeReportByStatus;

import java.util.List;

public interface DetaineeRepositoryCustom {
    Page<DetaineeResponse> getWithPaging(QueryDetaineeRequest request, Pageable pageable);
    List<DetaineeReportByStatus> getDetaineeReportByStatus(DetaineeReportStatus request);
    List<DetaineeReportByMonth> getDetaineeReportByMonth(DetaineeReportStatus request);
}
