package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.FingerprintImpressionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FingerprintImpressionRepository extends JpaRepository<FingerprintImpressionEntity,Long> {
    @Query(value = "SELECT f FROM FingerprintImpressionEntity f " +
            "left join FingerprintCardEntity fc on f.fingerprintCardId = fc.id " +
            "WHERE fc.id = :fingerprintCardId")
    List<FingerprintImpressionEntity> findByFingerprintCardId(@Param("fingerprintCardId") Long fingerprintCardId);

    @Query(value = "SELECT f FROM FingerprintImpressionEntity f " +
            "left join FingerprintCardEntity fc on f.fingerprintCardId = fc.id " +
            "WHERE f.fingerprintCardId = :fingerprintCardId AND f.kind = :kind AND f.finger = :finger")
    Optional<FingerprintImpressionEntity> findByFingerprintCardIdAndKindAndFinger(
            @Param("fingerprintCardId") Long fingerprintCardId,
            @Param("kind") String kind,
            @Param("finger") String finger);

    void deleteByFingerprintCardId(Long fingerprintCardId);
}
