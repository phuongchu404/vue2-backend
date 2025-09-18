package vn.mk.eid.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.response.StaffResponse;
import vn.mk.eid.web.dto.response.report.StaffReportByDepartment;

import java.util.List;

public interface StaffRepositoryCustom {
    Page<StaffResponse> getWithPaging(QueryStaffRequest request, Pageable pageable);
    List<StaffReportByDepartment> getReportByDepartment(DetaineeReportStatus request);
}
