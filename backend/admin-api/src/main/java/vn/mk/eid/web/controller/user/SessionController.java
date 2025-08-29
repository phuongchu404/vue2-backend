package vn.mk.eid.web.controller.user;

import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.RoleVO;
import vn.mk.eid.common.response.UserVO;
import vn.mk.eid.user.service.PermissionRoleService;
import vn.mk.eid.user.service.UserService;
import vn.mk.eid.user.utils.CurrentUser;
import vn.mk.eid.web.controller.user.data.AuthenticationPassword;
import vn.mk.eid.web.controller.user.data.ChangePasswordRequest;
import vn.mk.eid.web.controller.user.data.LoginRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * desc
 *
 * @author liukeshao
 * @date 2017/10/24
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final UserService userService;

    private final PermissionRoleService permissionRoleService;

    public SessionController(UserService userService, PermissionRoleService permissionRoleService) {
        this.userService = userService;
        this.permissionRoleService = permissionRoleService;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ServiceResult<UserVO> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        ServiceResult<UserVO> result = userService.login(request.getUsername(), request.getPassword(), request.getOtp());
        if (result.isSuccess() && result.getData() != null) {
            UserVO vo = result.getData();
            response.setHeader("x-access-token", vo.getToken());
        }
        return result;
    }

    @GetMapping("/permission/tag")
    public ServiceResult<List<String>> queryPermissionTags() {
        UserVO loginUser = CurrentUser.getLoginUser();
        List<Integer> ids = loginUser.getRoles().stream().map(RoleVO::getId).collect(Collectors.toList());
        return permissionRoleService.listTagsByRoleIds(ids);
    }

    @PostMapping("passwordchange")
    public ServiceResult<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        UserVO loginUser = CurrentUser.getLoginUser();
        return userService.changePasswordByUsername(loginUser.getUserName(), request.getOldPassword(), request.getNewPassword());
    }
}
