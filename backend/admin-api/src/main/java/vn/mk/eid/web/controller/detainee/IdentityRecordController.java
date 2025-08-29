package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.IdentityRecordCreateRequest;
import vn.mk.eid.web.dto.request.IdentityRecordUpdateRequest;
import vn.mk.eid.web.dto.request.QueryIdentityRecordRequest;
import vn.mk.eid.web.service.IdentityRecordService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/admin/identity-record")
@RequiredArgsConstructor
public class IdentityRecordController {
    private final IdentityRecordService identityRecordService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new identity record", description = "Create a new identity record")
    public ServiceResult createIdentityRecord(
            @Valid @RequestPart("payload") IdentityRecordCreateRequest request,
            @RequestPart(value = "front", required = false) MultipartFile front,
            @RequestPart(value = "leftProfile", required = false) MultipartFile leftProfile,
            @RequestPart(value = "rightProfile", required = false) MultipartFile rightProfile
        ) {
        return identityRecordService.createIdentityRecord(request, front, leftProfile, rightProfile);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update identity record", description = "Update an existing identity record")
    public ServiceResult updateIdentityRecord(
            @NotNull @PathVariable("id") Long id,
            @Valid @RequestPart("payload") IdentityRecordUpdateRequest request,
            @RequestPart(value = "front", required = false) MultipartFile front,
            @RequestPart(value = "leftProfile", required = false) MultipartFile leftProfile,
            @RequestPart(value = "rightProfile", required = false) MultipartFile rightProfile
        ) {
        return identityRecordService.updateIdentityRecord(id, request, front, leftProfile, rightProfile);
    }

    @GetMapping("/{id}")
    public ServiceResult getIdentityRecord(@PathVariable Long id) {
        return identityRecordService.getIdentityRecord(id);
    }

    @GetMapping("/detainee/{detaineeId}")
    public ServiceResult getIdentityRecordByDetaineeId(@PathVariable("detaineeId") Long detaineeId) {
        return identityRecordService.getIdentityRecordByDetaineeId(detaineeId);
    }

    @GetMapping()
    @Operation(summary = "Get identity record with paging", description = "Retrieve paginated list of identity record")
    public ServiceResult getIdentityRecordWithPaging(
        QueryIdentityRecordRequest request,
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "1") int pageNo,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return identityRecordService.getIdentityRecordWithPaging(request, pageable);
    }
}
