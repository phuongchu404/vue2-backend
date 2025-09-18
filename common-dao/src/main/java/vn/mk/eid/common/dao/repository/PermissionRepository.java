package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.mk.eid.common.dao.entity.PermissionEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {
    @Query(value = "select p from PermissionEntity p where p.type in :types")
    List<PermissionEntity> findAllByTypes(@Param("types") List<String> types);

    @Query(value = "select p from PermissionEntity p where p.type = :type order by p.tag asc")
    List<PermissionEntity> findAllByTypeOrderByAsc(@Param("type") String type);

    @Query(value = "select p from PermissionEntity p where p.tag in :tags")
    List<PermissionEntity> findAllByTags(@Param("tags") List<String> tags);

    @Query(value = "select p from PermissionEntity p order by p.tag asc")
    List<PermissionEntity> findAllOrderByAsc();

    @Transactional
    void deleteByTagNotIn(Collection<String> tags);

    Optional<PermissionEntity> findByTag(String tag);
}
