package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;


public class ChangePasswordRequest {
    @Setter @Getter
    private String oldPassword;
    @Setter @Getter
    private String newPassword;
}
