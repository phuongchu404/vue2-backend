package vn.mk.eid.common.enums;


import vn.mk.eid.common.data.AuditLogEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mk
 * @date 06-Aug-2025
 */
public enum AuditEvent {
    CREATE("Tạo mới"),
    UPDATE("Cập nhật"),
    DELETE("Xóa");
    String name;

    AuditEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<AuditLogEvent> getEvent() {
        List<AuditLogEvent> events = new ArrayList<>();
        for (AuditEvent e : values()) {
            events.add(new AuditLogEvent(e.name));
        }
        return events;
    }
}
