package vn.mk.eid.web.controller.detainee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.administrative_region.QueryAdministrativeRegionRequest;
import vn.mk.eid.web.service.AdministrativeRegionService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin/administrative-region")
public class AdministrativeRegionController {
    private final AdministrativeRegionService administrativeRegionService;

    @GetMapping("/all")
    private ServiceResult getAllRegion(QueryAdministrativeRegionRequest request) {
        return administrativeRegionService.getAll(request);
    }
}
