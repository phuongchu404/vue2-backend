package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.mk.eid.common.dao.entity.RoleEntity;
import vn.mk.eid.common.response.RoleResponse;
import vn.mk.eid.common.response.RoleVO;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByRoleName(String roleName);

    @Query(value = "select new vn.mk.eid.common.response.RoleResponse(r.id,r.roleName,r.description,r.createTime,r.updateTime,r.removable) " +
            "from RoleEntity r " +
            "where lower(r.roleName) like %:roleName%  order by r.id")
    Page<RoleResponse> searchRoleByRoleName(@Param("roleName") String roleName, Pageable pageable);

    Optional<RoleEntity> findById(Integer id);

    @Query(value = "select new vn.mk.eid.common.response.RoleVO(r.id,r.roleName,r.description,r.removable) from RoleEntity r where lower(r.roleName) like %:roleName% ")
    List<RoleVO> searchRoleByRoleName(@Param("roleName") String roleName);

    @Query(value = "select new vn.mk.eid.common.response.RoleVO(r.id,r.roleName,r.description,r.removable) from RoleEntity r inner join UserRoleEntity ur on r.id = ur.roleId where ur.userId = :userId")
    List<RoleVO> findByUserId(@Param("userId") Integer userId);

    @Query(value = "select r from RoleEntity r where r.id in :roleIds")
    List<RoleEntity> findAllByIds(@Param("roleIds") List<Integer> roleIds);

    @Query(value = "select r from RoleEntity r where r.id = :roleId and r.roleName = :roleName")
    List<RoleEntity> findAllByIdAndRoleName(@Param("roleId") Integer roleId, @Param("roleName") String roleName);

    @Query(value = "select new vn.mk.eid.common.response.RoleVO(r.id,r.roleName,r.description,r.removable) from RoleEntity r where r.roleName in :roleNames")
    List<RoleVO> findByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query(value = "select new vn.mk.eid.common.response.RoleVO(r.id,r.roleName,r.description,r.removable) from RoleEntity r where r.roleName = :roleName")
    Optional<RoleVO> getRoleVOByRoleName(@Param("roleName") String roleName);

    @Query(value = "select r from RoleEntity r order by r.roleName")
    List<RoleEntity> getListRole();
}
