package vn.mk.eid.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.mk.eid.common.dao.entity.UserEntity;
import vn.mk.eid.common.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    @Query(value = "select new vn.mk.eid.common.dto.UserDto(u.id,u.userName,u.realName,u.mail,u.createTime,u.updateTime,u.removable,u.phoneNumber,u2.realName, u.description) " +
            "from UserEntity u " +
            "left join UserEntity u2 on u.createUser = u2.id " +
            "where lower(u.userName) like %:userName% " +
            "order by u.userName ")
    Page<UserDto> findAllUsersVO(@Param("userName") String userName, Pageable pageable);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findById(Integer id);

    @Query(value = "select u from UserEntity u where u.id in :ids")
    List<UserEntity> findAllByIds(@Param("ids") List<Integer> ids);
}
