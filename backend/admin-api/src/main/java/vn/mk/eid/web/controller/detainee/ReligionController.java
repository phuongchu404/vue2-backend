package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.service.ReligionService;

@RestController
@RequestMapping("/api/admin/religion")
@RequiredArgsConstructor
public class ReligionController {
    private final ReligionService religionService;

    @GetMapping("/all")
    @Operation(summary = "Get all Religions", description = "Get all Religions")
    public ServiceResult getAllReligions(@RequestParam(required = false) String keyword) {
        return religionService.findAll(keyword);
    }
}
