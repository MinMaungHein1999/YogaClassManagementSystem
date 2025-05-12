package com.yogiBooking.common.service.yogaClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YogaClassBookingCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String COUNT_KEY_PREFIX = "yoga_class:";
    private static final String WAITLIST_KEY_SUFFIX = ":waitlist";

    public List<String> getWaitlist(Long classId) {
        return redisTemplate.opsForList().range(COUNT_KEY_PREFIX + classId + WAITLIST_KEY_SUFFIX, 0, -1);
    }

    public Long getCurrentBookingCount(Long classId) {
        String key = COUNT_KEY_PREFIX + classId + ":count";
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0L : Long.parseLong(value);
    }


    public Long incrementBookingCount(Long classId) {
        return redisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + classId + ":count");
    }

    public Long decrementBookingCount(Long classId) {
        return redisTemplate.opsForValue().decrement(COUNT_KEY_PREFIX + classId + ":count");
    }

    public void pushToWaitlist(Long classId, Long yogiId) {
        redisTemplate.opsForList().rightPush(COUNT_KEY_PREFIX + classId + WAITLIST_KEY_SUFFIX, yogiId.toString());
    }

    public String popFromWaitlist(Long classId) {
        return redisTemplate.opsForList().leftPop(COUNT_KEY_PREFIX + classId + WAITLIST_KEY_SUFFIX);
    }
}
