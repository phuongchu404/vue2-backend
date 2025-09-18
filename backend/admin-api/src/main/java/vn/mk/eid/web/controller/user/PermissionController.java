package vn.mk.eid.web.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.dao.entity.PermissionEntity;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.BeanMapper;
import vn.mk.eid.user.service.PermissionService;
import vn.mk.eid.web.controller.user.data.AddPermissionRequest;

import java.util.List;
@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public ServiceResult<Boolean> synchronizePermission(@RequestBody List<AddPermissionRequest> requests) {
        List<PermissionEntity> permissions = BeanMapper.listCopy(requests, PermissionEntity.class);
        return  permissionService.syncPermissions(permissions);
    }
}
