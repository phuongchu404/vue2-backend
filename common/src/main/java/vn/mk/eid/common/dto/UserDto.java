package vn.mk.eid.common.dto;

import lombok.Data;
import vn.mk.eid.common.response.RoleVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserDto implements Serializable {
    private Integer id;
    private String avatar = "/images/user.png";
    private String userName;
    private String realName;
    private String mail;
    private Date createTime;
    private Date updateTime;
    private Integer removable = 1;
    private List<RoleVO> roles;
    Integer idsRole;
    private String phoneNumber;
    private String createUser;
    private String description;

    public UserDto(Integer id, String userName, String realName, String mail, Date createTime, Date updateTime, Integer removable, String phoneNumber, String createUser, String description) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.mail = mail;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.removable = removable;
        this.phoneNumber = phoneNumber;
        if (createUser == null || createUser.length() == 0) {
            this.createUser = "System";
        } else {
            this.createUser = createUser;
        }
        this.description = description;
    }
}
