package vn.mk.eid.web.controller.user;

import lombok.extern.slf4j.Slf4j;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.PermissionVO;
import vn.mk.eid.user.service.PermissionRoleService;
import vn.mk.eid.web.controller.user.data.UpdateRolePermissionsRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/roleperms")
public class RolePermissionController {
    private final PermissionRoleService permissionRoleService;

    public RolePermissionController(PermissionRoleService permissionRoleService) {
        this.permissionRoleService = permissionRoleService;
    }

    @GetMapping("/{roleId}")
    public ServiceResult<List<PermissionVO>> listAvailPermsOfRole(@PathVariable Integer roleId) {
        log.info("Get Permission Of Role by RoleId: {}", roleId);
        return permissionRoleService.listTagsByRoleId(roleId);
    }


    @PutMapping("/{roleId}")
    public ServiceResult<Boolean> updatePermsForRoleId(@RequestBody UpdateRolePermissionsRequest request, @PathVariable Integer roleId) {
        log.info("Assign Permission For Role by RoleId: {}", roleId);
        return permissionRoleService.updatePermsForRoleId(roleId, request.getSelectedPermissions());
    }

}
