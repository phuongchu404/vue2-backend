package vn.mk.eid.common.dao.entity;

import lombok.Data;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "SYS_ROLE")
@EntityListeners(value = {AuditLogListener.class})
public class RoleEntity implements AuditAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "ROLE_NAME")
    String roleName;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "CREATE_USER_ID")
    Long createUserId;
    @Column(name = "CREATE_TIME")
    Date createTime;
    @Column(name = "UPDATE_TIME")
    Date updateTime;
    @Column(name = "REMOVABLE")
    Integer removable;
    @Override
    public String getMessage() {
        return "Vai trò [Tên=" + roleName + "]";
    }

    @Transient
    Integer userId;

    @Override
    public Integer getUserIdAudit() {
        return userId;
    }
}
