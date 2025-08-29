package vn.mk.eid.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mk.eid.common.dao.entity.PermissionEntity;
import vn.mk.eid.common.dao.entity.PermissionRoleEntity;
import vn.mk.eid.common.dao.repository.PermissionRepository;
import vn.mk.eid.common.dao.repository.PermissionRoleRepository;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.user.service.PermissionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {
    private PermissionRepository permissionRepository;
    private PermissionRoleRepository permissionRoleRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionRoleRepository permissionRoleRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionRoleRepository = permissionRoleRepository;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ServiceResult<Boolean> batchSaveOrUpdate(List<PermissionEntity> permissions) {
        List<String> tags = new ArrayList<>();
        for (PermissionEntity permission : permissions)
            tags.add(permission.getTag());
        List<PermissionEntity> permissionsOld = permissionRepository.findAllByTags(tags);
        for (PermissionEntity permissionOld : permissionsOld) {
            permissionRepository.delete(permissionOld);
        }
        Integer adminRoleId = 1;
        permissions.forEach(permission -> {
            Optional<PermissionRoleEntity> permissionRoleEntityOptional = permissionRoleRepository.findAllByRemovableAndTag(adminRoleId, permission.getTag());
            if (!permissionRoleEntityOptional.isPresent()) {
                PermissionRoleEntity rolePermission = new PermissionRoleEntity();
                rolePermission.setRoleId(adminRoleId);
                rolePermission.setTag(permission.getTag());
                rolePermission.setCreateTime(new Date());
                permissionRoleRepository.save(rolePermission);
            }
        });
        return ServiceResult.ok(Boolean.TRUE);
    }

    @Override
    public ServiceResult listAllPermissions() {
        return ServiceResult.ok(permissionRepository.findAllOrderByAsc());
    }

    @Override
    public ServiceResult<Boolean> updatePermission(PermissionEntity permission) {
        return null;
    }

    @Override
    @Transactional
    public ServiceResult<Boolean> delete(Integer id) {
        Optional<PermissionEntity> permission = permissionRepository.findById(id);
        if (!permission.isPresent()) return ServiceResult.fail(ResultCode.PERMISSION_NOT_FOUND);
        permissionRepository.delete(permission.get());
        return ServiceResult.ok();
    }

    @Override
    public ServiceResult<Boolean> addPermission(PermissionEntity permission) {
        permissionRepository.save(permission);
        return ServiceResult.ok(true);
    }

    @Override
    public List<String> getTagRole(Integer type) {
        List<PermissionEntity> permissions = new ArrayList<>();
        if (type != 0) {
            permissions = permissionRepository.findAllByTypeOrderByAsc(type.toString());
        } else {
            permissions = permissionRepository.findAllOrderByAsc();
        }
        List<String> tags = new ArrayList<>();
        for (PermissionEntity permission : permissions)
            tags.add(permission.getTag());
        return tags;
    }

    @Override
    public List<String> getTagRole() {
        List<PermissionEntity> permissions = permissionRepository.findAllOrderByAsc();
        List<String> tags = new ArrayList<>();
        for (PermissionEntity permission : permissions)
            tags.add(permission.getTag());
        return tags;
    }
}
