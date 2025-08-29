package vn.mk.eid.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mk.eid.common.dao.entity.PermissionEntity;
import vn.mk.eid.common.dao.entity.PermissionRoleEntity;
import vn.mk.eid.common.dao.entity.UserRoleEntity;
import vn.mk.eid.common.dao.repository.PermissionRepository;
import vn.mk.eid.common.dao.repository.PermissionRoleRepository;
import vn.mk.eid.common.dao.repository.UserRepository;
import vn.mk.eid.common.dao.repository.UserRoleRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.PermissionVO;
import vn.mk.eid.user.service.PermissionRoleService;
import vn.mk.eid.user.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
@Service
public class PermissionRoleServiceImpl implements PermissionRoleService {
    RedisTemplate redisTemplate;
    PermissionRepository permissionRepository;
    UserRoleRepository userRoleRepository;
    UserRepository userRepository;
    PermissionRoleRepository permissionRoleRepository;

    @Autowired
    UserService userService;

    public PermissionRoleServiceImpl(RedisTemplate redisTemplate, PermissionRoleRepository permissionRoleRepository, PermissionRepository permissionRepository, UserRoleRepository userRoleRepository, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRoleRepository = permissionRoleRepository;
    }

    @Override
    public ServiceResult listTagsByRoleIds(List<Integer> roleIds) {
        List<PermissionRoleEntity> rolePermissions = permissionRoleRepository.findAllByRoleIds(roleIds);
        List<String> tags = rolePermissions.stream().map(PermissionRoleEntity::getTag).collect(Collectors.toList());
        return ServiceResult.ok(tags);
    }

    @Override
    public ServiceResult<List<String>> listTagsByRoleIds(List<Integer> roleIds, List<String> tagsRole) {
        List<PermissionRoleEntity> rolePermissions = permissionRoleRepository.findAllByRoleIdsAndTags(roleIds, tagsRole);
        List<String> tags = rolePermissions.stream().map(PermissionRoleEntity::getTag).collect(Collectors.toList());
        return ServiceResult.ok(tags);
    }

    @Override
    public ServiceResult<List<PermissionVO>> listTagsByRoleId(Integer roleId) {
        List<PermissionRoleEntity> rolePermissions = permissionRoleRepository.findAllByRoleId(roleId);
        List<String> tags = rolePermissions.stream().map(PermissionRoleEntity::getTag).collect(Collectors.toList());
        if (tags.size() == 0)
            return ServiceResult.ok(new ArrayList<>());

        List<PermissionEntity> permissions = permissionRepository.findAllByTags(tags);
        List<String> buttonTags = permissions.stream().filter(permission -> "button".equals(permission.getType())).map(PermissionEntity::getTag).collect(Collectors.toList());

        List<PermissionVO> permissionVOS = new ArrayList<>();
        for (PermissionRoleEntity rolePermission : rolePermissions) {
            if (buttonTags.contains(rolePermission.getTag()) && !"index".equals(rolePermission.getTag())) {
                permissionVOS.add(new PermissionVO(rolePermission.getTag(), rolePermission.getRemovable()));
            }
        }
        return ServiceResult.ok(permissionVOS);
    }

    @Override
    @Transactional
    public ServiceResult<Boolean> updatePermsForRoleId(Integer roleId, List<String> tags) {
        List<PermissionRoleEntity> oldPerms = findByRoleId(roleId);
        permissionRoleRepository.deleteAll(oldPerms);
        List<PermissionRoleEntity> permissionRoleEntities = tags.stream().map(tag -> {
            PermissionRoleEntity po = new PermissionRoleEntity();
            po.setRoleId(roleId);
            po.setTag(tag);
            po.setCreateTime(new Date());
            return po;
        }).collect(Collectors.toList());
        permissionRoleRepository.saveAll(permissionRoleEntities);
        redisTemplate.convertAndSend("channel_update_permissions", "anything");
        List<UserRoleEntity> userRoles = userRoleRepository.findAllUserRole(roleId);
        if (userRoles.size() == 0) return ServiceResult.ok(Boolean.TRUE);
        List<Integer> userIds = getRoleUser(userRoles);
        userService.clearTokenByUserIds(userIds);
        return ServiceResult.ok(Boolean.TRUE);
    }

    private List<PermissionRoleEntity> findByRoleId(Integer roleId) {
        return permissionRoleRepository.findAllByRoleId(roleId);
    }

    List<Integer> getRoleUser(List<UserRoleEntity> userRoles) {
        List<Integer> userIds = new ArrayList<>();
        for (UserRoleEntity userRole : userRoles) userIds.add(userRole.getUserId());
        return userIds;
    }
}
