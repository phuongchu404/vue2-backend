package vn.mk.eid.web.controller.user.data;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String realName;
    private String mail;
    private String phoneNumber;
    private Integer unitId;
    private String description;
}
