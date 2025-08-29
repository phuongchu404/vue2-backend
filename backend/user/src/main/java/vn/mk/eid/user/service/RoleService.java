package vn.mk.eid.user.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.RoleVO;

import java.util.List;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface RoleService {
    ServiceResult<Boolean> addRole(String roleName, String description);

    ServiceResult<Boolean> deleteRoleById(int id);

    ServiceResult<Boolean> updateRoleById(Integer roleId, String roleName, String description);

    ServiceResult searchRoles(String roleName, int pageNo, int pageSize);

    RoleVO getRoleVOByRoleName(String roleName);

    List<Integer> getRoleIdByRoleName(List<String> roleNames);

    ServiceResult getListRole();
}
