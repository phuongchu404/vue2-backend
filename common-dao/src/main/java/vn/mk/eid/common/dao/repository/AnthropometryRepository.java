package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.AnthropometryEntity;

import java.util.Optional;

@Repository
public interface AnthropometryRepository extends JpaRepository<AnthropometryEntity, Long> {
    Optional<AnthropometryEntity> findByIdentityRecordId(Long identityRecordId);

    void deleteByIdentityRecordId(Long id);
}
