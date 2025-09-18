package vn.mk.eid.common.dao.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@ToString
@Table(name = "SYS_USER")
@EntityListeners(value = {AuditLogListener.class})
public class UserEntity implements AuditAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "USER_NAME")
    String userName;
    @Column(name = "REAL_NAME")
    String realName;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "SECRET")
    String secret;
//    @Column(name = "PROVIDER_CODE")
//    String providerCode;
    @Column(name = "TWO_STEP")
    Integer twoStep;
    @Column(name = "LAST_LOGIN")
    Date lastLogin;
    @Column(name = "LAST_FAULTY_LOGIN")
    Date lastFaultyLogin;
    @Column(name = "CONS_PASS_FAULTY")
    Integer consPassFaulty;
    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    Date createTime;
    @Column(name = "CREATE_USER")
    Integer createUser;
    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    Date updateTime;
    @Column(name = "UPDATE_USER")
    Integer updateUser;
    @Column(name = "REMOVABLE")
    Integer removable;
    @Column(name = "MAIL")
    String mail;
    @Column(name = "PHONE_NUMBER")
    String phoneNumber;
    @Column(name = "detention_center_id")
    Integer detentionCenterId;

    @Transient
    Integer userIdAudit;

    @Override
    public String getMessage() {
        return "User [UserName = " + userName + "]";
    }

    @Override
    public Integer getUserIdAudit() {
        return userIdAudit;
    }
}
