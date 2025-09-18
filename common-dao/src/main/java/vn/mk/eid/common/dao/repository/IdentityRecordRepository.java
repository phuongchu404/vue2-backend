package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.IdentityRecordEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityRecordRepository extends JpaRepository<IdentityRecordEntity,Long>, JpaSpecificationExecutor<IdentityRecordEntity> {

    @Query(value = "select i from IdentityRecordEntity i " +
            "left join DetaineeEntity d on i.detaineeId = d.id " +
            "where d.id = :detaineeId")
    List<IdentityRecordEntity> findByDetaineeId(@Param("detaineeId") Long detaineeId);

    Page<IdentityRecordEntity> findByArrestDateBetween(LocalDate arrestDateAfter, LocalDate arrestDateBefore, Pageable pageable);

    @Query("SELECT ir FROM IdentityRecordEntity ir " +
            "LEFT JOIN PhotoEntity pt on pt.identityRecordId = ir.id " +
            "LEFT JOIN AnthropometryEntity ar on ar.identityRecordId = ir.id " +
            "WHERE ir.id = :id")
    Optional<IdentityRecordEntity> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "select count(id) " +
            " from identity_record " +
            " where ?1 is null OR created_at < DATE_TRUNC('month', CURRENT_DATE)", nativeQuery = true)
    Optional<Integer> getTotalIdentity(Boolean isPreviousMonth);

    @Query("SELECT COUNT(ir) FROM IdentityRecordEntity ir ")
    Long countAll();

    @Query("SELECT COUNT(i) FROM IdentityRecordEntity i WHERE DATE(i.createdAt) BETWEEN :startDate AND :endDate")
    Long countIdentityInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
