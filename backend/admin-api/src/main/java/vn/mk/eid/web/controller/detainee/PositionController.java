package vn.mk.eid.web.controller.detainee;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryPositionRequest;
import vn.mk.eid.web.service.PositionService;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/position")
public class PositionController {
    private final PositionService positionService;

    @GetMapping("/all")
    @Operation(summary = "Get all position", description = "Get all position")
    public ServiceResult getWithPaging(QueryPositionRequest request) {

        return positionService.getAll();
    }
}
