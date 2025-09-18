package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.PermissionRoleEntity;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface PermissionRoleRepository extends JpaRepository<PermissionRoleEntity, Integer> {
    List<PermissionRoleEntity> findAllByTag(String tag);

    @Query(value = "select pr from PermissionRoleEntity pr where pr.tag in :tags")
    List<PermissionRoleEntity> findAllByTags(@Param("tags") List<String> tags);

    @Query(value = "select pr from PermissionRoleEntity pr where pr.roleId in :roleIds")
    List<PermissionRoleEntity> findAllByRoleIds(@Param("roleIds") List<Integer> roleIds);

    @Query(value = "select pr from PermissionRoleEntity pr where pr.roleId in :roleIds and pr.tag in :tags")
    List<PermissionRoleEntity> findAllByRoleIdsAndTags(@Param("roleIds") List<Integer> roleIds, @Param("tags") List<String> tags);

    @Query(value = "select pr from PermissionRoleEntity pr where pr.roleId = :roleId and pr.tag = :tag")
    Optional<PermissionRoleEntity> findAllByRoleIdAndTag(@Param("roleId") Integer roleId, @Param("tag") String tag);

    List<PermissionRoleEntity> findAllByRoleId(Integer roleId);

    @Modifying
    @Transactional
    void deleteByRoleId(Integer roleId);

    @Modifying
    @Transactional
    void deleteByTagNotIn(List<String> tags);
}
