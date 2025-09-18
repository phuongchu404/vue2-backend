package vn.mk.eid.web.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.report.OverviewStatistics;
import vn.mk.eid.web.dto.report.ReportInsight;
import vn.mk.eid.web.dto.report.ReportResponse;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
//    ServiceResult getReportOverview();
//    ServiceResult getDetaineeReportByStatus(DetaineeReportStatus request);
//    ServiceResult getDetaineeReportByMonth(DetaineeReportStatus request);
//    ServiceResult getStaffReportByDepartment(DetaineeReportStatus request);

    OverviewStatistics getOverviewStatistics();

    // Main report generation method
//    ReportResponse generateReport(String type, LocalDate fromDate, LocalDate toDate);
//
//    // Specific report methods
    ReportResponse getDetaineesByStatusReport(LocalDate fromDate, LocalDate toDate);
    ReportResponse getDetaineesByMonthReport(LocalDate fromDate, LocalDate toDate);
    ReportResponse getStaffByDepartmentReport();
    ReportResponse getIdentityRecordsReport(LocalDate fromDate, LocalDate toDate);
    ReportResponse getFingerprintCardsReport(LocalDate fromDate, LocalDate toDate);
    ReportResponse getMonthlySummaryReport(LocalDate month);
//
//    // Department-specific reports (new)
    ReportResponse getDepartmentTrendsReport(Integer departmentId, LocalDate fromDate, LocalDate toDate);
    ReportResponse getDepartmentComparisonReport(LocalDate reportDate);
//
//    // Export methods
    byte[] exportReportToExcel(String type, LocalDate fromDate, LocalDate toDate);
    byte[] exportReportToPDF(String type, LocalDate fromDate, LocalDate toDate);
//
//    // Advanced analytics
    List<ReportInsight> getReportInsights(String reportType, LocalDate fromDate, LocalDate toDate);

    // Cache management
    void refreshReportCache(String reportType);
    void clearAllReportCache();
}
