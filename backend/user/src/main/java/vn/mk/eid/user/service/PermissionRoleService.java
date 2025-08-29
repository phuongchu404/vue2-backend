package vn.mk.eid.user.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.PermissionVO;

import java.util.List;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface PermissionRoleService {
    ServiceResult listTagsByRoleIds(List<Integer> roleIds);

    ServiceResult<List<String>> listTagsByRoleIds(List<Integer> roleIds, List<String> tagsRole);

    ServiceResult<List<PermissionVO>> listTagsByRoleId(Integer roleId);

    ServiceResult<Boolean> updatePermsForRoleId(Integer roleId, List<String> tags);

}
