package vn.mk.eid.common.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "SYS_USER_ROLE")
@EntityListeners(value = {AuditLogListener.class})
public class UserRoleEntity implements AuditAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "USER_ID")
    Integer userId;
    @Column(name = "ROLE_ID")
    Integer roleId;
    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    Date createTime;
    Integer removable;

    @Transient
    Integer userIdAudit;

    @Override
    public String getMessage() {
        return "User-Role [UserID=" + userId + ", RoleID=" + roleId + "]";
    }

    @Override
    public Integer getUserIdAudit() {
        return userIdAudit;
    }
}
