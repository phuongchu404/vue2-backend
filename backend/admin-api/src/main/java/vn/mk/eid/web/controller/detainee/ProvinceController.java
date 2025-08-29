package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryProvinceRequest;
import vn.mk.eid.web.service.ProvinceService;

@RestController
    @RequestMapping("/api/admin/province")
@RequiredArgsConstructor
public class ProvinceController {
    private final ProvinceService provinceService;

    @GetMapping("/all")
    @Operation(summary = "Get all Provinces", description = "Get all Provinces")
    public ServiceResult getAllProvinces(QueryProvinceRequest request) {
         return provinceService.findAll(request);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get Province by code", description = "Get Province by code")
    public ServiceResult getProvinceByCode(@PathVariable String code) {
        return provinceService.findProvinceByCode(code);
    }

}
