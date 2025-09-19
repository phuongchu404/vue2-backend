package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.FingerprintCardEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FingerprintCardRepository extends JpaRepository<FingerprintCardEntity,Long> {

    @Query(value = "SELECT fc FROM FingerprintCardEntity fc " +
            "inner join DetaineeEntity d on fc.personId = d.id " +
            "WHERE fc.personId = :personId")
    Optional<FingerprintCardEntity> findByDetaineeId(@Param("personId") Long personId);

    Page<FingerprintCardEntity> findByCreatedDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<FingerprintCardEntity> findByPersonIdAndIdIsNot(Long personId, Long fingerprintCardId);

    @Query(value = "select count(id) " +
            " from fingerprint_card " +
            " where ?1 is null OR created_at < DATE_TRUNC('month', CURRENT_DATE)", nativeQuery = true)
    Optional<Integer> getTotalFingerCard(Boolean isPreviousMonth);

    @Query("SELECT COUNT(fc) FROM FingerprintCardEntity fc WHERE DATE(fc.createdDate) BETWEEN :startDate AND :endDate")
    Long countFingerprintInPeriod(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);


}
