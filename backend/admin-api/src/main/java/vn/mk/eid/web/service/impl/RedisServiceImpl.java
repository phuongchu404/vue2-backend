package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.mk.eid.web.service.RedisService;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    @Override
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValueWithExpireTime(String key, Object value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void removeValue(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void putToHash(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public Object getFromHash(String key, String field) {
        return hashOperations.get(key, field);
    }

    @Override
    public Long incrementHash(String key, String field) {
        return hashOperations.increment(key, field, 1);
    }
}
