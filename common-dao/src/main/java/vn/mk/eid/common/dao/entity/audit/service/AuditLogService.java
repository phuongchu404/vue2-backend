package vn.mk.eid.common.dao.entity.audit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.AuditLogEntity;
import vn.mk.eid.common.dao.repository.AuditLogRepository;

@Service
@Slf4j
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void save(String persistedObjectIdm, String eventName, String oldValue, String newValue, String message, Integer userId, String tableName, String status) {
        try {
            log.info("Save a new audit");
            AuditLogEntity auditLog = new AuditLogEntity(persistedObjectIdm, eventName, oldValue, newValue, message, userId, tableName, status);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
