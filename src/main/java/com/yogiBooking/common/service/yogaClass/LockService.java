package com.yogiBooking.common.service.yogaClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean lock(String key, long expireSeconds) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", Duration.ofSeconds(expireSeconds))
        );
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
