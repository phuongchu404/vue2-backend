package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String otp;
}
