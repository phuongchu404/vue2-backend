package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.request.staff.StaffCreateRequest;
import vn.mk.eid.web.dto.request.staff.StaffUpdateRequest;
import vn.mk.eid.web.excel.DetaineeExportExcel;
import vn.mk.eid.web.excel.StaffExportExcel;
import vn.mk.eid.web.service.StaffService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/staff")
public class StaffController {
    private final StaffService staffService;

    @GetMapping
    @Operation(summary = "Get staff with paging", description = "Retrieve paginated list of staff")
    public ServiceResult getStaffWithPaging(
            QueryStaffRequest request,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return staffService.getStaffWithPaging(request, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff by ID", description = "Retrieve staff information by ID")
    public ServiceResult getStaffById(
            @Parameter(description = "Staff ID") @PathVariable Integer id) {
        return staffService.getStaffById(id);
    }

    @PostMapping("/create")
    @Operation(summary = "Create new staff", description = "Create a new staff record")
    public ServiceResult createStaff(
            @Valid @RequestBody StaffCreateRequest request) {
        return staffService.createStaff(request);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update staff", description = "Update an existing staff record")
    public ServiceResult updateStaff(
            @Parameter(description = "Staff ID") @PathVariable Integer id,
            @Valid @RequestBody StaffUpdateRequest request) {
        return staffService.updateStaff(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete staff", description = "Delete a staff record")
    public ServiceResult deleteStaff(
            @Parameter(description = "Staff ID") @PathVariable Integer id) {
        return staffService.deleteStaff(id);
    }

    @GetMapping("/export")
    @Operation(summary = "Export staffs to Excel", description = "Export staff data to an Excel file"
    )
    public void exportStaffToExcel(
            QueryStaffRequest request,
            HttpServletResponse response) {
        try {

            StaffExportExcel exporter = new StaffExportExcel(staffService);
            exporter.exportMultiSheet(response, request);
        } catch (Exception e) {
            log.error("Error exporting staffs to Excel", e);
            throw new RuntimeException("Error exporting staffs to Excel: " + e.getMessage());
        }
    }

    @GetMapping("/get-top-3-newest")
    @Operation(summary = "Get top 3 newest staffs", description = "Retrieve the top 3 newest staffs")
    public ServiceResult getTop3NewestStaffs() {
        return staffService.findTop3NewestStaffs();
    }

    @GetMapping("/count")
    @Operation(summary = "Count total staffs", description = "Retrieve the total count of staffs")
    public ServiceResult countStaffs() {
        return staffService.countStaffs();
    }
}
