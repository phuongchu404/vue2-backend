package vn.mk.eid.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AUDIT_LOG")
@Entity
public class AuditLogEntity {
    @Id
    @Column(name = "ID", length = 100)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DATE_CREATED")
    @CreationTimestamp
    private Date dateCreated;
    @Column(name = "PERSISTED_OBJECT_ID")
    private String persistedObjectId;
    @Column(name = "EVENT_NAME")
    private String eventName;
    @Column(name = "TABLE_NAME")
    private String tableName;
    @Column(name = "OLD_VALUE")
    private String oldValue;
    @Column(name = "NEW_VALUE")
    private String newValue;
    @Column(name = "MESSAGE")
    private String message;
    @Column(name = "USER_ID")
    private Integer userId;
    @Column(name = "STATUS")
    private String status;

    public AuditLogEntity(String persistedObjectIdm, String eventName, String oldValue, String newValue, String message, Integer userId,  String tableName, String status) {
        this.persistedObjectId = persistedObjectIdm;
        this.eventName = eventName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.message = message;
        this.userId = userId;
        this.tableName = tableName;
        this.status = status == null ? "SUCCESS" : status;
    }
}
