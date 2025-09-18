package vn.mk.eid.common.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "SYS_ROLE_PERMISSION")
@EntityListeners(value = {AuditLogListener.class})
public class PermissionRoleEntity implements AuditAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "ROLE_ID")
    Integer roleId;

    @Column(name = "TAG")
    String tag;

    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    Date createTime;

    @Column(name = "REMOVABLE")
    Integer removable;

    @Override
    public String getMessage() {
        return "Quyền [Tên=" + tag + "] cho vai trò [ID=" + roleId + "]";
    }

    @Transient
    Integer userId;

    @Override
    public Integer getUserIdAudit() {
        return userId;
    }
}
