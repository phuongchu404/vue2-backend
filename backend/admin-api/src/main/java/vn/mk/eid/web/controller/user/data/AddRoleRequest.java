package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;

public class AddRoleRequest {
    @Getter @Setter
    private String roleName;

    @Getter @Setter
    private String description;
}
