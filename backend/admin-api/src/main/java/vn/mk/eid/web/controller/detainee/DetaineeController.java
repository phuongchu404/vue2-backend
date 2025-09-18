package vn.mk.eid.web.controller.detainee;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.detainee.DetaineeCreateRequest;
import vn.mk.eid.web.dto.request.detainee.DetaineeUpdateRequest;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.response.excel.DetaineeExcelDTO;
import vn.mk.eid.web.excel.DetaineeExportExcel;
import vn.mk.eid.web.service.DetaineeService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/detainee")
public class DetaineeController {

    private final DetaineeService detaineeService;

    @PostMapping("/create")
    @Operation(summary = "Create new detainee", description = "Create a new detainee record")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string"))
    })
    public ServiceResult createDetainee(
            @Valid @RequestBody DetaineeCreateRequest request) {
        return detaineeService.createDetainee(request);

    }

    @PutMapping("/{id}")
    @Operation(summary = "Update detainee", description = "Update an existing detainee record")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult updateDetainee(
            @Parameter(description = "Detainee ID") @PathVariable Long id,
            @Valid @RequestBody DetaineeUpdateRequest request) {
        return detaineeService.updateDetainee(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detainee by ID", description = "Retrieve detainee information by ID")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult getDetainee(
            @Parameter(description = "Detainee ID") @PathVariable Long id) {
        return detaineeService.getDetainee(id);
    }

    @GetMapping("/code/{detaineeCode}")
    @Operation(summary = "Get detainee by code", description = "Retrieve detainee information by code")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult getDetaineeByCode(
            @Parameter(description = "Detainee Code") @PathVariable String detaineeCode) {
        return detaineeService.getDetaineeByCode(detaineeCode);
    }

    @GetMapping
    @Operation(summary = "Get all detainees", description = "Retrieve paginated list of all detainees")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult getAllDetainees(
            QueryDetaineeRequest request,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return detaineeService.getWithPaging(request, pageable);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete detainee", description = "Delete a detainee record")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult deleteDetainee(
            @Parameter(description = "Detainee ID") @PathVariable Long id) {
       return detaineeService.deleteDetainee(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all detainees without paging", description = "Retrieve list of all detainees without paging")
    public ServiceResult getAllDetaineesNoPaging() {
        return detaineeService.getAllNoPaging();
    }

    @GetMapping("/export")
    @Operation(summary = "Export detainees to Excel", description = "Export detainee data to an Excel file"
    )
    public void exportDetaineesToExcel(
            QueryDetaineeRequest request,
            HttpServletResponse response) {
        try {
//            String fileName = "Danh_sach_pham_nhan.xlsx";
//            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setCharacterEncoding("UTF-8");
//            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            DetaineeExportExcel exporter = new DetaineeExportExcel(detaineeService);
            exporter.exportMultiSheet(response, request);
        } catch (Exception e) {
            log.error("Error exporting detainees to Excel", e);
            throw new RuntimeException("Error exporting detainees to Excel: " + e.getMessage());
        }
    }

    @GetMapping("/get-top-3-newest")
    @Operation(summary = "Get top 3 newest detainees", description = "Retrieve the top 3 newest detainees")
    public ServiceResult getTop3NewestDetainees() {
        return detaineeService.getTop3NewestDetainees();
    }

    @GetMapping("/count")
    @Operation(summary = "Count total detainees", description = "Retrieve the total count of detainees")
    public ServiceResult countTotalDetainees() {
        return detaineeService.getDetaineeCount();
    }
}
