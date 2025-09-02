package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.service.WardService;

@RestController
@RequestMapping("/api/admin/ward")
@RequiredArgsConstructor
public class WardController {

    private final WardService wardService;

    @GetMapping("/all")
    @Operation(summary = "Get all Wards", description = "Get all Wards")
    public ServiceResult getAllWards(QueryWardRequest request) {
        return wardService.findAll(request);
    }

    @GetMapping("/by-code/{code}")
    @Operation(summary = "Get Ward by code", description = "Get Ward by code")
    public ServiceResult getWardByCode(String code) {
        return wardService.findByCode(code);
    }

    @GetMapping("/{provinceCode}")
    @Operation(summary = "Get Wards by province code", description = "Get Wards by province code")
    public ServiceResult getWardsByProvinceCode(@PathVariable("provinceCode") String provinceCode) {
        return wardService.findByProvinceCode(provinceCode);
    }
}
