package vn.mk.eid.web.controller.detainee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.administrative_unit.QueryAdministrativeUnitRequest;
import vn.mk.eid.web.service.AdministrativeUnitService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin/administrative-unit")
public class AdministrativeUnitController {
    private final AdministrativeUnitService administrativeUnitService;

    @GetMapping("/all")
    private ServiceResult getAllUnit(QueryAdministrativeUnitRequest request) {
        return administrativeUnitService.getAll(request);
    }
}
