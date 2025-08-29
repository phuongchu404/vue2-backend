package vn.mk.eid.common.response;

import lombok.Data;

import java.util.Date;

@Data
public class RoleResponse {
    private Integer id;
    private String roleName;
    private String description;
    private Date createTime;
    private Date updateTime;
    private Integer removable;

    public RoleResponse(Integer id, String roleName, String description, Date createTime, Date updateTime, Integer removable) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.removable = removable;
    }
}
