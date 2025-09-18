package vn.mk.eid.user.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import vn.mk.eid.common.dao.entity.PermissionEntity;
import vn.mk.eid.common.dao.entity.PermissionRoleEntity;
import vn.mk.eid.common.dao.repository.PermissionRepository;
import vn.mk.eid.common.dao.repository.PermissionRoleRepository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
public class PermissionCache implements MessageListener {
    private final PermissionRoleRepository permissionRoleRepository;
    private final PermissionRepository permissionRepository;
    private final Map<PermissionEntity, List<Integer>> cache = new HashMap<>();

    public PermissionCache(PermissionRoleRepository permissionRoleRepository, PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionRoleRepository = permissionRoleRepository;
    }

    public Map<PermissionEntity, List<Integer>> getCache() {
        return cache;
    }

    @PostConstruct
    private void initPermissionCache() {
        cache.clear();
        List<String> types = Arrays.asList("button", "api");
        List<PermissionEntity> permissions = permissionRepository.findAllByTypes(types);
        permissions.stream().filter(po -> !StringUtils.isEmpty(po.getPattern())).forEach(po -> {
            List<PermissionRoleEntity> rolePermissions = permissionRoleRepository.findAllByTag(po.getTag());
            List<Integer> roleIds = rolePermissions.stream().map(PermissionRoleEntity::getRoleId).collect(Collectors.toList());
            cache.put(po, roleIds);
        });
    }

    public boolean reinitPermissionCache() {
        cache.clear();
        List<String> types = Arrays.asList("button", "api");
        List<PermissionEntity> permissions = permissionRepository.findAllByTypes(types);
        permissions.stream().filter(po -> !StringUtils.isEmpty(po.getPattern())).forEach(po -> {
            List<PermissionRoleEntity> rolePermissions = permissionRoleRepository.findAllByTag(po.getTag());
            List<Integer> roleIds = rolePermissions.stream().map(PermissionRoleEntity::getRoleId).collect(Collectors.toList());
            cache.put(po, roleIds);
        });
        return true;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        initPermissionCache();
    }

    public boolean match(String method, String url, List<Integer> roleIds) {
        return !Collections.disjoint(getRoleIds(method, url), roleIds);
    }

    private List<Integer> getRoleIds(String method, String url) {
        List<Integer> roleIds = new ArrayList<>();
        Set<PermissionEntity> permissions = cache.keySet();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (PermissionEntity permission : permissions) {
            boolean urlMatch = antPathMatcher.match(permission.getPattern(), url);
            if (urlMatch && permission.getIsWhiteList() == true) {
                roleIds.addAll(cache.get(permission));
            }
            boolean methodMatch = permission.getMethod() == null || permission.getMethod().equalsIgnoreCase(method);
            if (urlMatch && methodMatch) {
                roleIds.addAll(cache.get(permission));
            }
        }
        return roleIds;
    }
}
