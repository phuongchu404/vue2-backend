package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.PhotoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity,Long> {

    @Query(value = "select p from PhotoEntity p " +
            "left join IdentityRecordEntity i on p.identityRecordId = i.id " +
            "where i.id = :identityRecordId")
    List<PhotoEntity> findByIdentityRecordId(@Param("identityRecordId") Long identityRecordId);

    @Query(value = "select p from PhotoEntity p " +
            "left join IdentityRecordEntity i on p.identityRecordId = i.id " +
            "where i.id = :identityRecordId and p.view = :view")
    Optional<PhotoEntity> findByIdentityRecordIdAndView(@Param("identityRecordId") Long identityRecordId, @Param("view") String view);
}
