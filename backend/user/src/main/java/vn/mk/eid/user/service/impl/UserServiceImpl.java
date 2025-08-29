package vn.mk.eid.user.service.impl;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.dao.entity.UserEntity;
import vn.mk.eid.common.dao.repository.RoleRepository;
import vn.mk.eid.common.dao.repository.UserRepository;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.dto.UserDto;
import vn.mk.eid.common.response.RoleVO;
import vn.mk.eid.common.response.UserVO;
import vn.mk.eid.common.util.CryptoUtil;
import vn.mk.eid.common.util.JwtUtil;
import vn.mk.eid.user.LoginProperties;
import vn.mk.eid.user.service.TokenService;
import vn.mk.eid.user.service.UserService;
import vn.mk.eid.user.utils.CurrentUser;
import vn.mk.eid.user.utils.TokenType;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ThangNh
 * @since 2024-07-26
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
    }

    @Override
    public ServiceResult login(String username, String password, String otp) {
        Optional<UserEntity> optional = userRepository.findByUserName(username);
        if (!optional.isPresent()) {
            return ServiceResult.fail(ResultCode.USER_NOT_FOUND);
        }
        UserEntity po = optional.get();
        if (po.getConsPassFaulty() >= LoginProperties.getMaxConsPassFaulty()) {
            Date date = po.getLastFaultyLogin();
            LocalDateTime lastFaultyLogin = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (lastFaultyLogin.plusSeconds(LoginProperties.getConsPassFaultyBlock()).isBefore(LocalDateTime.now())) {
                return ServiceResult.fail(ResultCode.CONS_PASS_FAULTY);
            } else {
                po.setConsPassFaulty(0);
            }
        }
        if (!CryptoUtil.verifyPassword(password, po.getPassword())) {
            po.setLastFaultyLogin(new Date());
            po.setConsPassFaulty(po.getConsPassFaulty() + 1);
            userRepository.save(po);
            return ServiceResult.fail(ResultCode.INCORRECT_PASSWORD);
        }
        Date loginTime = new Date();

        List<RoleVO> roles = roleRepository.findByUserId(po.getId());
        if (roles == null || roles.size() == 0)
            return ServiceResult.fail(ResultCode.ROLE_NOT_FOUND);

        po.setLastLogin(loginTime);
        po.setConsPassFaulty(0);
        userRepository.save(po);
        String token = tokenService.generateToken(po.getUserName(), po.getSecret()).successData();

        UserVO vo = new UserVO(po.getId(), po.getUserName(), po.getRealName(), po.getCreateTime(), po.getUpdateTime(), po.getLastLogin(), po.getRemovable());
        vo.setTwoStepEnabled(po.getTwoStep() == 1);
        vo.setVerifyOtp(false);

        vo.setRoles(roles);
        vo.setToken(token);
        vo.setTokenType(TokenType.tokenLogin);
        tokenService.storeToken(token, vo).successData();
        return ServiceResult.ok(vo);
    }

    @Override
    public ServiceResult<Boolean> verifyToken(String username, String token) {
        Optional<UserEntity> optional = userRepository.findByUserName(username);
        if (!optional.isPresent()) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND.getDescription());
        }
        UserEntity po = optional.get();
        Claims claims = JwtUtil.getClaims(token, po.getSecret());
        if (!username.equals(claims.getSubject())) {
            throw new ServiceException(ResultCode.INVALID_ACCESS_TOKEN.getDescription());
        }
        return ServiceResult.ok(true);
    }

    @Override
    public ServiceResult addUser(String username, String realName, String mail, String phoneNumber, Integer unitId, String description) {
        log.info("Starting add a new user with username = {}", username);
        Optional<UserEntity> optional = userRepository.findByUserName(username);
        if (optional.isPresent()) {
            return ServiceResult.fail(ResultCode.USER_ALREADY_EXISTED);
        }
        String newPassword = CryptoUtil.randomText(8);
        String encryptedPassword = CryptoUtil.encryptPassword(newPassword);
        UserEntity record = new UserEntity();
        record.setUserName(username);
        record.setRealName(realName);
        record.setMail(mail);
        record.setPhoneNumber(phoneNumber);
        record.setSecret(CryptoUtil.getRandomSecretKey());
        record.setPassword(encryptedPassword);
        record.setConsPassFaulty(0);
        record.setTwoStep(LoginProperties.getTwoStep());
        record.setUnitId(unitId);
        record.setDescription(description);
        record.setRemovable(1);
        record.setCreateUser(CurrentUser.getLoginUser().getId());

        userRepository.save(record);

        return ServiceResult.ok(newPassword);
    }

    @Override
    public ServiceResult<Boolean> updateUserByUserName(Integer userId, String realName, String description, String phoneNumber, Integer unitId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            if (user.getRemovable().equals(0)) return ServiceResult.fail(ResultCode.USER_CANNOT_BE_UPDATE);
            user.setRealName(realName);
            user.setDescription(description);
            user.setPhoneNumber(phoneNumber);
            user.setUpdateUser(CurrentUser.getLoginUser().getId());
            user.setUnitId(unitId);
            userRepository.save(user);
            return ServiceResult.ok(true);
        }
        return ServiceResult.fail(ResultCode.USER_NOT_FOUND);
    }

    @Override
    @Transactional
    public ServiceResult<Boolean> deleteUserById(Integer id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            if (user.getRemovable().equals(0)) return ServiceResult.fail(ResultCode.USER_CANNOT_BE_DELETED);
            userRepository.delete(user);
            return ServiceResult.ok();
        }
        return ServiceResult.fail(ResultCode.USER_NOT_FOUND);
    }

    @Override
    public ServiceResult resetUserPasswordById(Integer id, String pwd) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (!userEntity.isPresent()) return ServiceResult.fail(ResultCode.USER_NOT_FOUND);

        String encryptedPassword = CryptoUtil.encryptPassword(pwd);
        UserEntity user = userEntity.get();
        user.setPassword(encryptedPassword);
        user.setUpdateTime(new Date());
        userRepository.save(user);
        return ServiceResult.ok(true);
    }

    @Override
    public ServiceResult<Boolean> changePasswordByUsername(String userName, String oldPassword, String newPassword) {
        Optional<UserEntity> userEntity = userRepository.findByUserName(userName);
        if (!userEntity.isPresent()) {
            return ServiceResult.fail(ResultCode.USER_NOT_FOUND);
        }
        UserEntity po = userEntity.get();
        boolean verifyPassword = CryptoUtil.verifyPassword(oldPassword, po.getPassword());
        if (!verifyPassword) {
            return ServiceResult.fail(ResultCode.INCORRECT_PASSWORD);
        }
        String encryptedPassword = CryptoUtil.encryptPassword(newPassword);
        po.setPassword(encryptedPassword);
        po.setUpdateTime(new Date());
        userRepository.save(po);
        tokenService.clearToken(userName, po.getSecret());
        return ServiceResult.ok(true);
    }

    @Override
    public ServiceResult searchUser(String userName, int pageNo, int pageSize) {
        Page<UserDto> page = userRepository.findAllUsersVO(userName, PageRequest.of(pageNo - 1, pageSize));
        List<UserDto> contents = page.getContent();
        List<UserDto> result = new ArrayList<>();
        for (UserDto dto : contents) {
            List<RoleVO> roleVO = roleRepository.findByUserId(dto.getId());
            dto.setRoles(roleVO);
            result.add(dto);
        }
        Page<UserDto> pageResult = new PageImpl<>(result, page.getPageable(), page.getTotalElements());
        return ServiceResult.ok(pageResult);
    }

    @Override
    public Boolean clearTokenByUserIds(List<Integer> userIds) {
        List<UserEntity> users = userRepository.findAllByIds(userIds);
        for (UserEntity user : users)
            tokenService.clearToken(user.getUserName(), user.getSecret());
        return true;
    }
}
