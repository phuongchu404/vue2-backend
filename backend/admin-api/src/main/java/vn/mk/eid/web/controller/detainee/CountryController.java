package vn.mk.eid.web.controller.detainee;


import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.QueryWardRequest;
import vn.mk.eid.web.service.CountrySevice;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/country")
public class CountryController {
    private final CountrySevice countrySevice;

    public CountryController(CountrySevice countrySevice) {
        this.countrySevice = countrySevice;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all countries", description = "Retrieve a list of all countries")
    private ServiceResult getAllCountry(QueryWardRequest request) {
        return countrySevice.getAllCountry(request);
    }
}
