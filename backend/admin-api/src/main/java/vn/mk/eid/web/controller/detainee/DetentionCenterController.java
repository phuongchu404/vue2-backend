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
import vn.mk.eid.web.dto.request.DetentionCenterSearchRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterCreateRequest;
import vn.mk.eid.web.dto.request.detention_center.DetentionCenterUpdateRequest;
import vn.mk.eid.web.dto.request.detention_center.QueryDetentionCenterRequest;
import vn.mk.eid.web.service.DetentionCenterService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin/detention-center")
public class DetentionCenterController {

    private final DetentionCenterService detentionCenterService;

//    @GetMapping("/search")
//    @Operation(summary = "search detention center", description = "get all detention center")
//    public ServiceResult searchDetentionCenters(QueryDetentionCenterRequest request) {
//        log.info("Search detention centers");
//        return detentionCenterService.findAllDetentionCenters(request);
//    }

    @GetMapping("/all")
    @Operation(summary = "get all detention center", description = "get all detention center")
    public ServiceResult getAllDetentionCenters() {
        log.info("Fetching all detention centers");
        return detentionCenterService.findAllDetentionCenters();
    }

    @GetMapping("/search")
    @Operation(summary = "search detention center", description = "search detention center")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult searchDetentionCenters(DetentionCenterSearchRequest request, @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int pageNo,
                                                @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return detentionCenterService.searchDetentionCenters(request, pageable);
    }

    @PostMapping("/create")
    @Operation(summary = "Create new detention center", description = "Create a new detention center")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult createDetentionCenter(@Valid @RequestBody DetentionCenterCreateRequest request) {
        log.info("Creating detention center with name: {}", request.getName());
        return detentionCenterService.createDetentionCenter(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "get detention center by id", description = "get detention center by id")
    @Parameters({
            @Parameter(name = "x-access-token", in = ParameterIn.HEADER, required = true,
                    description = "Access token",
                    schema = @Schema(type = "string")),
    })
    public ServiceResult getDetentionCenterById(@Parameter(description = "detention center Id") @PathVariable Integer id) {
        log.info("Fetching detention center with ID: {}", id);
        return detentionCenterService.findDetentionCenterById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update detention center", description = "update detention center")
    public ServiceResult updateDetentionCenter(@PathVariable Integer id, @Valid @RequestBody DetentionCenterUpdateRequest request) {
        log.info("Updating detention center with ID: {}", id);
        return detentionCenterService.updateDetentionCenter(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete detention center", description = "delete detention center")
    public ServiceResult deleteDetentionCenter(@PathVariable Integer id) {
        log.info("Deleting detention center with ID: {}", id);
        return detentionCenterService.deleteDetentionCenter(id);
    }
}
