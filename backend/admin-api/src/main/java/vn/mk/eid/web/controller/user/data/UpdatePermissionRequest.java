package vn.mk.eid.web.controller.user.data;

import lombok.Data;

@Data
public class UpdatePermissionRequest {
//    Integer id;
    private String tag;
    private String type;
    private boolean whiteList;
    private String pattern;
    private String method;

}

