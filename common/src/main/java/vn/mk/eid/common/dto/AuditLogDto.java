package vn.mk.eid.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class AuditLogDto {
    Long id;
    @JsonIgnore
    private String persistedObjectId;
    private String eventName;
    private String message;
    private String username;
    private Map<String, String> oldValue;
    private Map<String, String> newValue;
    private String tableName;
    private String status;
    @JsonIgnore
    private String oldValueStr;
    @JsonIgnore
    private String newValueStr;
    private String createAt;

    public AuditLogDto(String eventName, String message) {
        this.eventName = eventName;
        this.message = message;
    }

    public AuditLogDto(Long id,  String eventName, String message, String oldValueStr, String newValueStr, String username,  String tableName, String status, Date createAt) {
        this.id = id;
        this.eventName = eventName;
        this.message = message;
        this.oldValueStr = oldValueStr;
        this.newValueStr = newValueStr;
        this.username = username;
        this.tableName = tableName;
        this.status = status;
        this.createAt = String.valueOf(createAt);
    }

    public AuditLogDto(Long id, String eventName, String message, String username,  String tableName, String status, Date createAt) {
        this.id = id;
        this.eventName = eventName;
        this.message = message;
        this.username = username;
        this.tableName = tableName;
        this.status = status;
        this.createAt = String.valueOf(createAt);
    }

    public AuditLogDto(String persistedObjectId, String eventName, String message, Map<String, String> oldValue, Map<String, String> newValue) {
        this.persistedObjectId = persistedObjectId;
        this.eventName = eventName;
        this.message = message;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
