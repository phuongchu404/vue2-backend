package vn.mk.eid.web.controller.detainee;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.detainee.DetaineeCreateRequest;
import vn.mk.eid.web.dto.request.detainee.DetaineeUpdateRequest;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.service.DetaineeService;

import javax.validation.Valid;

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
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

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

}
