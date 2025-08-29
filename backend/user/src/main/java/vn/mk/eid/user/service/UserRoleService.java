package vn.mk.eid.user.service;

import vn.mk.eid.common.dao.entity.UserRoleEntity;
import vn.mk.eid.common.data.ServiceResult;

import java.util.List;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface UserRoleService {
    ServiceResult<Boolean> updateRolesForUserId(Integer userId, List<Integer> roleIds);

    List<UserRoleEntity> findAllUserRole(Integer roleId);
}
