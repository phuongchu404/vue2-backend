package vn.mk.eid.common.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.mk.eid.common.dao.entity.audit.AuditAware;
import vn.mk.eid.common.dao.entity.audit.AuditLogListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Data
@Entity
@Table(name = "unit")
@EntityListeners(value = {AuditLogListener.class})
public class UnitEntity implements AuditAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_code", length = 25)
    private String unitCode;

    @Column(name = "unit_name", length = 50)
    private String unitName;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "ward_id")
    private Integer wardId;

    @CreationTimestamp
    @Column(name = "create_time")
    private Date createdTime;

    @Column(name = "create_user")
    private Integer createdUser;

    @Override
    public String getMessage() {
        return "Đơn vị [Tên = " + unitName + "]";
    }

    @Transient
    Integer userId;

    @Override
    public Integer getUserIdAudit() {
        return userId;
    }
}
