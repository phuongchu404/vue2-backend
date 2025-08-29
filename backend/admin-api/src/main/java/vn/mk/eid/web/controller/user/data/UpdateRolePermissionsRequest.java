package vn.mk.eid.web.controller.user.data;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UpdateRolePermissionsRequest {
    @Getter @Setter
    private Integer roleId;

    @Getter @Setter
    private List<String> selectedPermissions;
}
