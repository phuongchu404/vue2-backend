package vn.mk.eid.user.service;

import vn.mk.eid.common.data.ServiceResult;

import java.util.List;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface UserService {
    ServiceResult login(String username, String password, String otp);

    ServiceResult addUser(String username, String realName, String mail, String phoneNumber, Integer unitId, String description);

    ServiceResult<Boolean> updateUserByUserName(Integer userId, String realName, String description, String phoneNumber, Integer unitId);

    ServiceResult<Boolean> deleteUserById(Integer id);

    ServiceResult resetUserPasswordById(Integer id, String pwd);

    ServiceResult<Boolean> verifyToken(String username, String token);

    ServiceResult<Boolean> changePasswordByUsername(String username, String oldPassword, String newPassword);

    ServiceResult searchUser(String userName, int pageNo, int pageSize);

    Boolean clearTokenByUserIds(List<Integer> userIds);
}