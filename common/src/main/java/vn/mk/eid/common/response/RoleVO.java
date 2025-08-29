package vn.mk.eid.common.response;

import lombok.Data;

/**
 * @author mk
 * @date 05-Aug-2025
 */
@Data
public class RoleVO {
    private Integer id;
    private String roleName;
    private String description;
    private Integer removable;

    public RoleVO(Integer id, String roleName, String description, Integer removable) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.removable = removable;
    }
}
