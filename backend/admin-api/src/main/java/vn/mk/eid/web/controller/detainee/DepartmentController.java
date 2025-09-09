package vn.mk.eid.web.controller.detainee;


import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.department.DepartmentSaveRequest;
import vn.mk.eid.web.dto.request.department.QueryDepartmentRequest;
import vn.mk.eid.web.service.DepartmentService;

import javax.validation.Valid;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/department")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/all")
    @Operation(summary = "Get all department", description = "Get all department")
    public ServiceResult getWithPaging(QueryDepartmentRequest request) {
        return departmentService.getWithPaging(request);
    }

    @GetMapping("/{id}")
    public ServiceResult getWithPaging(@PathVariable Integer id) {
        return departmentService.getDetailById(id);
    }

    @GetMapping("/by-detention-center/{id}")
    @Operation(summary = "Get all department by detention center id", description = "Get all department by dentention center id")
    public ServiceResult getByDetentionCenter(@PathVariable Integer id) {
        return departmentService.getByDententionCenterId(id);
    }

    @PostMapping("/create")
    public ServiceResult createDepartment(@Valid @RequestBody DepartmentSaveRequest request) {
        return departmentService.createDepartment(request);
    }

    @PutMapping("/update/{id}")
    public ServiceResult updateDepartment(
            @Valid @RequestBody DepartmentSaveRequest request,
            @PathVariable Integer id
    ) {
        return departmentService.updateDepartment(request, id);
    }

    @DeleteMapping("/delete/{id}")
    public ServiceResult createDepartment(@PathVariable Integer id) {
        return departmentService.deleteDepartment(id);
    }
}
