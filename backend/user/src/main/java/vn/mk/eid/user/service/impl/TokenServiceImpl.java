package vn.mk.eid.user.service.impl;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.UserVO;
import vn.mk.eid.common.util.JwtUtil;
import vn.mk.eid.user.service.TokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mk
 * @since 2024-07-26
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Integer time = 60*60*2; // 2 hours

    public TokenServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public ServiceResult<String> generateToken(String username, String secret) {
        this.clearToken(username, secret);
        Claims claims = new DefaultClaims();
        claims.setSubject(username);
        String token = JwtUtil.getToken(claims, secret);
        return ServiceResult.ok(token);
    }

    @Override
    public ServiceResult<UserVO> loadToken(String token) {
        String json = redisTemplate.opsForValue().get(PREFIX_TOKEN + token);
        if (StringUtils.isEmpty(json)) {
            return ServiceResult.fail(ResultCode.INVALID_ACCESS_TOKEN);
        }
        Gson gson = new Gson();
        UserVO userVO = gson.fromJson(json, UserVO.class);
        return ServiceResult.ok(userVO);
    }

    @Override
    public ServiceResult<Boolean> storeToken(String token, UserVO userVO) {
        Gson gson = new Gson();
        redisTemplate.opsForValue().set(PREFIX_TOKEN + token, gson.toJson(userVO), time, TimeUnit.SECONDS);
        return ServiceResult.ok(true);
    }

    @Override
    public ServiceResult<Boolean> refreshToken(String token) {
        redisTemplate.boundGeoOps(PREFIX_TOKEN + token).expire(time, TimeUnit.SECONDS);
        return ServiceResult.ok(true);
    }

    @Override
    public ServiceResult<Boolean> clearToken(String userName, String secret) {
        Set<String> keys = redisTemplate.keys(PREFIX_TOKEN + "*");
        List<String> deletedKeys = new ArrayList<>();
        for (String key : keys) {
            String token = key.replace(PREFIX_TOKEN, "").trim();
            Claims claims = JwtUtil.getClaims(token, secret);
            if (claims == null) return null;
            if (userName.equals(claims.getSubject())) {
                deletedKeys.add(key);
            }
            break;
        }
        redisTemplate.delete(deletedKeys);
        return ServiceResult.ok(true);
    }
}
