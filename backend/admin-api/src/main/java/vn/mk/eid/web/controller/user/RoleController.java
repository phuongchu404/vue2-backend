package vn.mk.eid.web.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.user.service.RoleService;
import vn.mk.eid.web.controller.user.data.AddRoleRequest;
import vn.mk.eid.web.controller.user.data.QueryRoleRequest;
import vn.mk.eid.web.controller.user.data.UpdateRoleRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ServiceResult<Boolean> addRole(@RequestBody AddRoleRequest request) {
        return roleService.addRole(request.getRoleName(), request.getDescription());
    }


    @PutMapping("/{roleId}")
    public ServiceResult<Boolean> updateRoleById(@PathVariable Integer roleId, @RequestBody UpdateRoleRequest request) {
        log.info("Update Role width roleId: {}, description: {}", roleId, request.getDescription());
        return roleService.updateRoleById(roleId, request.getRoleName(), request.getDescription());
    }

    @DeleteMapping("/{roleId}")
    public ServiceResult<Boolean> deleteRoleById(@PathVariable Integer roleId) {
        return roleService.deleteRoleById(roleId);
    }

    @GetMapping
    public ServiceResult searchRoles(QueryRoleRequest roleRequest, @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Search Role width roleName: {}, pageNo: {}, pageSize: {}", roleRequest.getRoleName(), pageNo, pageSize);
        return roleService.searchRoles(roleRequest.getRoleName(), pageNo, pageSize);
    }

    @GetMapping("list")
    public ServiceResult listRole(){
        return roleService.getListRole();
    }
}
