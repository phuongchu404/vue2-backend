package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String username;
    private String realName;
    private String mail;
    private String phoneNumber;
    private Integer unitId;
    private String description;
}
