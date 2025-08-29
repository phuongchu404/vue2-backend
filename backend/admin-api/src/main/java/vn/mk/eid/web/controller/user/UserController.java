package vn.mk.eid.web.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.user.service.UserService;
import vn.mk.eid.web.controller.user.data.AddUserRequest;
import vn.mk.eid.web.controller.user.data.ResetPassword;
import vn.mk.eid.web.controller.user.data.SearchUserRequest;
import vn.mk.eid.web.controller.user.data.UpdateUserRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ServiceResult<Boolean> addUser(@RequestBody AddUserRequest request) {
        log.info("Add user width userName: {}, realName: {}, mail: {}, phoneNumber", request.getUsername(), request.getRealName(), request.getMail(), request.getPhoneNumber());
        return userService.addUser(request.getUsername(), request.getRealName(), request.getMail(), request.getPhoneNumber(), request.getUnitId(), request.getDescription());
    }


    @PutMapping("/{userId}")
    public ServiceResult<Boolean> updateUserById(@RequestBody UpdateUserRequest request, @PathVariable Integer userId) {
        log.info("Update User by id: {}, realName: {}, description: {}", userId, request.getRealName(), request.getDescription());
        return userService.updateUserByUserName(userId, request.getRealName(), request.getDescription(), request.getPhoneNumber(), request.getUnitId());
    }

    @DeleteMapping("/{userId}")
    public ServiceResult<Boolean> deleteUserById(@PathVariable Integer userId) {
        ServiceResult<Boolean> serviceResult = userService.deleteUserById(userId);
        return serviceResult;
    }

    @PostMapping("/passwordreset")
    public ServiceResult<String> resetUserPassword(@RequestBody ResetPassword resetPassword) {
        log.info("Reset Password width userId: {}", resetPassword.getId());
        return userService.resetUserPasswordById(resetPassword.getId(), resetPassword.getPassword());
    }

    @PostMapping("/password")
    public ServiceResult<String> resetPassword(@RequestBody ResetPassword resetPassword) {
        return userService.resetUserPasswordById(resetPassword.getId(), resetPassword.getPassword());
    }

    @GetMapping
    public ServiceResult searchUser(SearchUserRequest request, @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Search user width userName: {}, pageNo: {}, pageSize: {}", request.getUserName(), pageNo, pageSize);
        return userService.searchUser(request.getUserName(), pageNo, pageSize);
    }
}
