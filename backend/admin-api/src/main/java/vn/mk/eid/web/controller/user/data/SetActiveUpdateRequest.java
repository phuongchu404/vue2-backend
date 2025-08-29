package vn.mk.eid.web.controller.user.data;

import lombok.Data;

@Data
public class SetActiveUpdateRequest {
    String version;
    Integer status;
}
