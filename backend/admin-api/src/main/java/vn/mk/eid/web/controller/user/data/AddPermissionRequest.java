package vn.mk.eid.web.controller.user.data;

import lombok.Data;

/**
 * @author liukeshao
 * @date 2018/8/7 15:29
 */
@Data
public class AddPermissionRequest {
    private String tag;
    private String type;
    private Boolean isWhiteList;
    private String pattern;
    private String method;
}
