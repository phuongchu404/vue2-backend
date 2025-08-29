package vn.mk.eid.web.controller.detainee;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
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
    @Parameters({
            @Parameter(
                    name = "x-access-token",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Access token cho mỗi request",
                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            )
    })
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResult.class))
            )
    })
    private ServiceResult getAllCountry() {
        return countrySevice.getAllCountry();
    }
}
