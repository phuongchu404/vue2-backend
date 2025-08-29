package vn.mk.eid.web.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void setValue(String key, Object value);
    void setValueWithExpireTime(String key, Object value, long expireTime, TimeUnit timeUnit);
    Object getValue(String key);
    void removeValue(String key);

    void putToHash(String key, String field, Object value);
    Object getFromHash(String key, String field);
    Long incrementHash(String key, String field);
}
