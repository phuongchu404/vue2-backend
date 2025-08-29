package vn.mk.eid.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mk.eid.common.dao.entity.PermissionRoleEntity;
import vn.mk.eid.common.dao.entity.RoleEntity;
import vn.mk.eid.common.dao.entity.UserRoleEntity;
import vn.mk.eid.common.dao.repository.PermissionRoleRepository;
import vn.mk.eid.common.dao.repository.RoleRepository;
import vn.mk.eid.common.dao.repository.UserRepository;
import vn.mk.eid.common.dao.repository.UserRoleRepository;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.RoleVO;
import vn.mk.eid.user.service.RoleService;
import vn.mk.eid.user.utils.CurrentUser;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    RedisTemplate<String, String> redisTemplate;
    RoleRepository roleRepository;
    PermissionRoleRepository permissionRoleRepository;
    UserRoleRepository userRoleRepository;
    UserRepository userRepository;

    public RoleServiceImpl(RedisTemplate redisTemplate, RoleRepository roleRepository, UserRoleRepository userRoleRepository, UserRepository userRepository, PermissionRoleRepository permissionRoleRepository) {
        this.redisTemplate = redisTemplate;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRoleRepository = permissionRoleRepository;
    }

    @Override
    public ServiceResult<Boolean> addRole(String roleName, String description) {
        Optional<RoleEntity> optional = findByRoleName(roleName);
        if (optional.isPresent()) {
            return ServiceResult.fail(ResultCode.ROLE_ALREADY_EXISTED);
        }
        RoleEntity record = new RoleEntity();
        record.setRoleName(roleName);
        record.setDescription(description);
        record.setCreateUserId(Long.valueOf(CurrentUser.getLoginUser().getId()));
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setRemovable(1);
        roleRepository.save(record);
        return ServiceResult.ok(Boolean.TRUE);
    }

    @Override
    @Transactional
    public ServiceResult<Boolean> deleteRoleById(int id) {
        long count = getUserCountOfRole(id);
        if (count > 0) {
            return ServiceResult.fail(ResultCode.ROLE_HAS_USERS);
        }
        Optional<RoleEntity> optionalRole = roleRepository.findById(id);
        if (!optionalRole.isPresent()) return ServiceResult.fail(ResultCode.ROLE_NOT_FOUND);
        RoleEntity role = optionalRole.get();
        if (role.getRemovable() == 0) {
            return ServiceResult.fail(ResultCode.ROLE_CANNOT_BE_DELETED);
        }
        List<PermissionRoleEntity> permissionRoles = permissionRoleRepository.findAllByRoleId(id);
        permissionRoleRepository.deleteAll(permissionRoles);

        roleRepository.delete(role);
        redisTemplate.convertAndSend("channel_update_permissions", "anything");
        return ServiceResult.ok(true);
    }

    @Override
    public RoleVO getRoleVOByRoleName(String roleName) {
        Optional<RoleVO> roleVO = roleRepository.getRoleVOByRoleName(roleName);
        if (roleVO.isPresent()) return roleVO.get();
        return null;
    }

    @Override
    public List<Integer> getRoleIdByRoleName(List<String> roleNames) {
        List<RoleVO> roleVOS = roleRepository.findByRoleNames(roleNames);
        return roleVOS.stream().map(RoleVO::getId).collect(Collectors.toList());
    }

    @Override
    public ServiceResult getListRole() {
        return ServiceResult.ok(roleRepository.getListRole());
    }

    @Override
    public ServiceResult<Boolean> updateRoleById(Integer roleId, String roleName, String description) {
        boolean exist = checkExist(roleId, roleName);
        if (!exist) {
            return ServiceResult.fail(ResultCode.ROLE_ALREADY_EXISTED);
        }
        RoleEntity po = findByRoleId(roleId).get();
        if (po.getRemovable().equals(0)) return ServiceResult.fail(ResultCode.ROLE_CANNOT_BE_UPDATE);
        po.setDescription(description);
        po.setUpdateTime(new Date());
        roleRepository.save(po);
        return ServiceResult.ok(Boolean.TRUE);
    }

    private boolean checkExist(Integer roleId, String roleName) {
        List<RoleEntity> list = roleRepository.findAllByIdAndRoleName(roleId, roleName);
        return !list.isEmpty();
    }

    @Override
    public ServiceResult searchRoles(String roleName, int pageNo, int pageSize) {
        return ServiceResult.ok(roleRepository.searchRoleByRoleName(roleName, PageRequest.of(pageNo - 1, pageSize)));
    }

    private Optional<RoleEntity> findByRoleName(String roleName) {
        Optional<RoleEntity> optionalRole = roleRepository.findByRoleName(roleName);
        if (!optionalRole.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(optionalRole.get());
    }

    private Optional<RoleEntity> findByRoleId(Integer roleId) {
        Optional<RoleEntity> optionalRole = roleRepository.findById(roleId);
        if (!optionalRole.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(optionalRole.get());
    }

    private long getUserCountOfRole(Integer roleId) {
        List<UserRoleEntity> list = userRoleRepository.findAllUserRole(roleId);
        int count = 0;
        for (UserRoleEntity userRole : list) {
            if (userRepository.findById(userRole.getUserId()).isPresent()) count++;
        }
        return count;
    }
}
