package vn.mk.eid.web.dto.request;

import lombok.Data;

@Data
public class DetentionCenterSearchRequest {
    private String code;
    private String name;
    private Boolean isActive;
}
