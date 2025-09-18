package vn.mk.eid.web.controller.detainee;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

//    @GetMapping("/overview")
//    public ServiceResult getReportOverview() {
//        return reportService.getReportOverview();
//    }
//
//    @GetMapping("/detainee-by-status")
//    public ServiceResult getDetaineeReportByStatus(DetaineeReportStatus request) {
//        return reportService.getDetaineeReportByStatus(request);
//    }
//
//    @GetMapping("/detainee-by-month")
//    public ServiceResult getDetaineeReportByMonth(DetaineeReportStatus request) {
//        return reportService.getDetaineeReportByMonth(request);
//    }
//
//    @GetMapping("/staff-by-department")
//    public ServiceResult getStaffReportByDepartment(DetaineeReportStatus request) {
//        return reportService.getStaffReportByDepartment(request);
//    }
}
