package vn.mk.eid.user.service;

import vn.mk.eid.common.dao.entity.PermissionEntity;
import vn.mk.eid.common.data.ServiceResult;

import java.util.List;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface PermissionService {
    ServiceResult<Boolean> batchSaveOrUpdate(List<PermissionEntity> permissions);

    ServiceResult listAllPermissions();

    ServiceResult<Boolean> updatePermission(PermissionEntity permission);

    ServiceResult<Boolean> delete(Integer id);

    ServiceResult<Boolean> addPermission(PermissionEntity permission);

    List<String> getTagRole(Integer type);

    List<String> getTagRole();
}
