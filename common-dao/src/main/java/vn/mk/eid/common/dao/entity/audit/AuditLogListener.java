package vn.mk.eid.common.dao.entity.audit;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;
import vn.mk.eid.common.dao.entity.audit.service.AuditLogService;
import vn.mk.eid.common.enums.AuditEvent;
import vn.mk.eid.common.util.ApplicationContextProvider;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuditLogListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {
    Gson gson = new Gson();

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    public String convertData(String[] propertyNames, Object[] states) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < propertyNames.length; i++) {
            data.put(propertyNames[i], states[i] == null ? "" : states[i].toString());
        }
        return gson.toJson(data);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof AuditAware) {
            log.info("Starting write logs for insert action with {}", event.getEntity().toString());
            AbstractEntityPersister persister = (AbstractEntityPersister) event.getPersister();
            String tableName = persister.getTableName();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider.getApplicationContext().getBean("auditLogService");
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] states = event.getState();
            String message = AuditEvent.CREATE.getName() + " " + ((AuditAware) entity).getMessage();
            Integer userId = ((AuditAware) entity).getUserIdAudit();
            auditLogService.save(event.getId().toString(), AuditEvent.CREATE.getName(), null, convertData(propertyNames, states), message, userId, tableName, "SUCCESS");
            log.info("Ending write logs for insert action...");
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof AuditAware) {
            AbstractEntityPersister persister = (AbstractEntityPersister) event.getPersister();
            String tableName = persister.getTableName();
            log.info("Starting write logs for update action with {}", event.getEntity().toString());
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] currentState = event.getState();
            Object[] previousState = event.getOldState();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider.getApplicationContext().getBean("auditLogService");
            String message = AuditEvent.UPDATE.getName() + " " + ((AuditAware) entity).getMessage();
            String dataOld = convertData(propertyNames, previousState);
            String dataNew = convertData(propertyNames, currentState);
            Integer userId = ((AuditAware) entity).getUserIdAudit();
            auditLogService.save(event.getId().toString(), AuditEvent.UPDATE.getName(), dataOld, dataNew, message, userId, tableName, "SUCCESS");
            log.info("Ending write logs for update action...");
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        Object entity = event.getEntity();
        AbstractEntityPersister persister = (AbstractEntityPersister) event.getPersister();
        String tableName = persister.getTableName();
        if (entity instanceof AuditAware) {
            log.info("Starting write logs for delete action with {}", event.getEntity().toString());
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] state = event.getDeletedState();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider.getApplicationContext().getBean("auditLogService");
            String message = AuditEvent.DELETE.getName() + " " + ((AuditAware) entity).getMessage();
            Integer userId = ((AuditAware) entity).getUserIdAudit
                    ();
            auditLogService.save(event.getId().toString(), AuditEvent.DELETE.getName(), convertData(propertyNames, state), null, message, userId,
                    tableName, "SUCCESS");
            log.info("Ending write logs for delete action...");
        }
    }
}
