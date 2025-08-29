package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UpdateUserRolesRequest {
    @Getter @Setter
    private Integer userId;

    @Getter @Setter
    private List<Integer> selectedRoles;
}
