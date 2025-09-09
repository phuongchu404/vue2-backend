package vn.mk.eid.web.dto.response;

import lombok.Data;

@Data
public class DepartmentResponse {
    private Integer id;
    private String name;
    private String code;
    private Integer detentionCenterId;
    private String detentionCenterCode;
    private String detentionCenterName;
    private String description;
    private Boolean isActive = Boolean.TRUE;
}
