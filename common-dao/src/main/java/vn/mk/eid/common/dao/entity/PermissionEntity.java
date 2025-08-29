package vn.mk.eid.common.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SYS_PERMISSION")
@Data
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "TAG")
    String tag;

    @Column(name = "TYPE")
    String type;

    @Column(name = "METHOD")
    String method;

    @Column(name = "PATTERN")
    String pattern;

    @Column(name = "IS_WHITE_LIST")
    Integer isWhiteList;

    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    Date createTime;
}
