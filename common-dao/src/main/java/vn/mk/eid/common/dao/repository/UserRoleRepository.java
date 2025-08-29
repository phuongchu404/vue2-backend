package vn.mk.eid.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.mk.eid.common.dao.entity.UserRoleEntity;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {

    List<UserRoleEntity> findByUserId(Integer userId);

    List<UserRoleEntity> findByRoleId(Integer roleId);

    @Modifying
    @Transactional
    void deleteAllByUserId(Integer userId);

    @Query(value = "select ur from UserRoleEntity ur where ur.roleId = :roleId")
    List<UserRoleEntity> findAllUserRole(@Param("roleId") Integer roleId);
}
