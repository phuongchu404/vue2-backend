package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.service.EthnicityService;

@RestController
@RequestMapping("/api/admin/ethnicity")
@RequiredArgsConstructor
public class EthnicityController {
    private final EthnicityService ethnicityService;

    @GetMapping("/all")
    @Operation(summary = "Get all ethnicities", description = "Get all ethnicities")
    public ServiceResult getAllEthnicities(@RequestParam(required = false) String keyword) {
        return ethnicityService.findAll(keyword);
    }
}
