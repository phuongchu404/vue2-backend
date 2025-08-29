package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.mk.eid.common.dao.entity.AuditLogEntity;
import vn.mk.eid.common.dto.AuditLogDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    @Query(value = "select new vn.mk.eid.common.dto.AuditLogDto(a.id, a.eventName, a.message,u.userName,  a.tableName, a.status, a.dateCreated) from AuditLogEntity a " +
            "left join UserEntity u on a.userId = u.id " +
            "where ( :message is null or lower(a.message) like %:message%) and (:event is null or lower(a.eventName) like %:event%) " +
            "and  a.dateCreated >= :from and  a.dateCreated <= :to order by a.id desc ")
    Page<AuditLogDto> findPageByEventAndMessage(@Param("event") String event, @Param("message") String message, @Param("from") Date from, @Param("to") Date to, Pageable pageable);


    @Query(value = "select new vn.mk.eid.common.dto.AuditLogDto(a.id, a.eventName, a.message, a.oldValue, a.newValue, u.userName,  a.tableName, a.status, a.dateCreated) from AuditLogEntity a " +
            "left join UserEntity u on a.userId = u.id " +
            "where :id = a.id")
    Optional<AuditLogDto> findDetail(@Param("id") Long id);


    @Query(value = "select new vn.mk.eid.common.dto.AuditLogDto(a.id, a.eventName, a.message,a.oldValue, a.newValue, u.userName,  a.status,a.tableName, a.dateCreated) from AuditLogEntity  a " +
            "left join UserEntity u on a.userId = u.id " +
            "where ( :message is null or lower(a.message) like %:message%) and (:event is null or lower(a.eventName) like %:event%) " +
            "and (a.dateCreated is null or (:from is null or a.dateCreated >= :from) and (:to is null or a.dateCreated <= :to)) order by a.dateCreated desc ")
    List<AuditLogDto> findAllByEventAndMessage(@Param("event") String event, @Param("message") String message, @Param("from") Date from, @Param("to") Date to);

}
