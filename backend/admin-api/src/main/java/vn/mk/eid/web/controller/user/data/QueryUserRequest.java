package vn.mk.eid.web.controller.user.data;

import lombok.Getter;
import lombok.Setter;

public class QueryUserRequest {
    @Getter @Setter
    private String userName;
    @Getter @Setter
    private String realName;

    @Getter @Setter
    private Long startTime;

    @Getter @Setter
    private Long endTime;
}
