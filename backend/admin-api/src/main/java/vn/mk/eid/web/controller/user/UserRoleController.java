package vn.mk.eid.web.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.user.service.UserRoleService;
import vn.mk.eid.web.controller.user.data.UpdateUserRolesRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin/userroles")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PutMapping("/{userId}")
    public ServiceResult<Boolean> updatePermsForRoleId(@RequestBody UpdateUserRolesRequest request, @PathVariable Integer userId) {
        log.info("Assign role by userId: {}", userId);
        return userRoleService.updateRolesForUserId(userId, request.getSelectedRoles());
    }
}
