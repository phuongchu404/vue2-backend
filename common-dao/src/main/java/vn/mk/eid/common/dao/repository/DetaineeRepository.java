package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DetaineeEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author mk
 * @date 06-Aug-2025
 */
@Repository
public interface DetaineeRepository extends JpaRepository<DetaineeEntity, Long> {

    Page<DetaineeEntity> findAll(Pageable pageable);

    Optional<DetaineeEntity> findByDetaineeCode(String detaineeCode);

    Page<DetaineeEntity> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<DetaineeEntity> findByDetentionCenterId(Integer id, Pageable pageable);

    Page<DetaineeEntity> findByStatus(String status, Pageable pageable);

    @Query("SELECT d FROM DetaineeEntity d WHERE d.detentionDate BETWEEN :startDate AND :endDate")
    List<DetaineeEntity> findByDetentionDateBetween(@Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate);

//    long countByDetentionCenterAndStatus(DetentionCenter detentionCenter, String status);
//
//    long countByStatus(String status);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d " +
            "inner join DetentionCenterEntity dt on d.detentionCenterId = dt.id " +
            "WHERE dt.id = :centerId AND d.status = 'DETAINED'")
    long countCurrentDetainees(@Param("centerId") Integer centerId);

    List<DetaineeEntity> findByIdIn(Collection<Long> detaineeIds);

    @Query(value = "WITH latest_history AS ( " +
            "    SELECT " +
            "        detainee_id, " +
            "        detention_center_id, " +
            "        type, " +
            "        start_date, " +
            "        ROW_NUMBER() OVER (PARTITION BY detainee_id ORDER BY start_date DESC) AS rn " +
            "    FROM detention_history " +
            ") " +
            "SELECT COUNT(DISTINCT detainee_id) AS currently_detained_count " +
            "FROM latest_history " +
            "WHERE rn = 1 AND type = 'INITIAL' AND (?1 IS NULL OR start_date < ?2) ", nativeQuery = true)
    Optional<Integer> getTotalDetainee(Boolean isPreviousMonth, LocalDate toDate);

    @Query(value = "select d from DetaineeEntity d where d.status = :status order by d.updatedAt desc")
    List<DetaineeEntity> findTop3OrderByUpdatedAtDesc(@Param("status") String status, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d WHERE d.status = 'DETAINED'")
    Long countByStatusDetained();


    @Query("SELECT COUNT(d) FROM DetaineeEntity d WHERE d.status = 'DETAINED'")
    Long countDetainedDetainees();

    @Query("SELECT COUNT(d) FROM DetaineeEntity d " +
            "WHERE d.status = 'RELEASED' AND d.actualReleaseDate <> null " +
            "AND DATE(d.actualReleaseDate) BETWEEN :startDate AND :endDate")
    Long countReleasedDetaineeInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d WHERE DATE(d.detentionDate) BETWEEN :startDate AND :endDate")
    Long countDetaineesInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT d.status, COUNT(*) AS cnt, " +
            "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM detainees), 2) AS percentage " +
            "FROM detainees d GROUP BY d.status",
            nativeQuery = true)
    List<Object[]> getDetaineeStatusStatistics();

    @Query("SELECT COUNT(d) FROM DetaineeEntity d " +
            "WHERE d.status = 'DETAINED' AND DATE(d.detentionDate) BETWEEN :startDate AND :endDate")
    Long countDetainedInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d inner join IdentityRecordEntity ir on d.id = ir.detaineeId")
    Long countDetaineesWithIdentityRecords();

    @Query("SELECT COUNT(d) FROM DetaineeEntity d inner join FingerprintCardEntity fc on d.id = fc.personId")
    Long countDetaineesWithFingerprintCards();

    @Query("SELECT d.detaineeCode, d.fullName FROM DetaineeEntity d WHERE d.detentionDate BETWEEN :startDate AND :endDate")
    List<Object[]> findDetaineesForIdentityRecords(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d WHERE d.status = 'TRANSFERRED' AND DATE(d.createdAt) BETWEEN :startDate AND :endDate")
    Long countTransferredDetaineesInPeriod(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(d) FROM DetaineeEntity d WHERE d.status = 'DETAINED' AND d.detentionDate <= :endOfMonth")
    Long countActiveDetaineesAsOfDate(@Param("endOfMonth") LocalDate endOfMonth);
}
