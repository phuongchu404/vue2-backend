package vn.mk.eid.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mk.eid.common.dao.entity.UserRoleEntity;
import vn.mk.eid.common.dao.repository.UserRoleRepository;
import vn.mk.eid.common.data.RemovableEnum;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.user.service.UserRoleService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
@Service
public class UserRoleServiceImpl implements UserRoleService {
    UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public ServiceResult<Boolean> updateRolesForUserId(Integer userId, List<Integer> roleIds) {
        List<UserRoleEntity> userRoleDeletes = userRoleRepository.findByUserId(userId);
        for (UserRoleEntity userRoleDelete : userRoleDeletes)
            if (userRoleDelete.getRemovable() != RemovableEnum.NOTREMOVE.getCode())
                userRoleRepository.delete(userRoleDelete);
        List<UserRoleEntity> pos = roleIds.stream().map(roleId -> {
            UserRoleEntity po = new UserRoleEntity();
            po.setRoleId(roleId);
            po.setUserId(userId);
            po.setCreateTime(new Date());
            return po;
        }).collect(Collectors.toList());
        userRoleRepository.saveAll(pos);
        return ServiceResult.ok(Boolean.TRUE);
    }

    @Override
    public List<UserRoleEntity> findAllUserRole(Integer roleId) {
        return userRoleRepository.findByRoleId(roleId);
    }
}
