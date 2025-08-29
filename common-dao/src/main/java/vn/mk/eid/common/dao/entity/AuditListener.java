package vn.mk.eid.common.dao.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class AuditListener {

    @PrePersist
    private void beforePrePersist(Object object) {
        System.out.println("beforePrePersist");
    }

    @PreUpdate
    private void beforePreUpdate(Object object) {
        System.out.println("beforePreUpdate");
    }

    @PreRemove
    private void beforePreRemove(Object object) {
        System.out.println("beforePreRemove");
    }

}
