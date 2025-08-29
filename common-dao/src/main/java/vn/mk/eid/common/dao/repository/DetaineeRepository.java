package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.DetaineeEntity;

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
}
