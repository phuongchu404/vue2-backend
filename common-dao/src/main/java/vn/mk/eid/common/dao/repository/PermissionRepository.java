package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.mk.eid.common.dao.entity.PermissionEntity;

import java.util.List;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {
    @Query(value = "select p from PermissionEntity p where p.type = :type")
    List<PermissionEntity> findAllByType(@Param("type") String type);

    @Query(value = "select p from PermissionEntity p where p.type = :type order by p.tag asc")
    List<PermissionEntity> findAllByTypeOrderByAsc(@Param("type") String type);

    @Query(value = "select p from PermissionEntity p where p.tag in :tags")
    List<PermissionEntity> findAllByTags(@Param("tags") List<String> tags);

    @Query(value = "select p from PermissionEntity p order by p.tag asc")
    List<PermissionEntity> findAllOrderByAsc();
}
