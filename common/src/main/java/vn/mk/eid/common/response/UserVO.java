package vn.mk.eid.common.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author mk
 * @date 05-Aug-2025
 */

@Data
@NoArgsConstructor
public class UserVO implements Serializable {
    private static final long serialVersionUID = 5183419576868236510L;

    private Integer id;
    private String avatar = "../../../assets/user.png";
    private String userName;
    private String realName;
    private Date createTime;
    private Date updateTime;
    private Integer removable;
    private boolean twoStepEnabled;
    private boolean verifyOtp;
    private List<RoleVO> roles;
    private String token;
    private Date lastLogin;
    private Integer type;
    private Integer tokenType;
    private Integer dententionCenterId;

    public UserVO(Integer id, String userName, String realName, Date createTime, Date updateTime, Date lastLogin, Integer removable) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.lastLogin = lastLogin;
        this.removable = removable;
    }
}
