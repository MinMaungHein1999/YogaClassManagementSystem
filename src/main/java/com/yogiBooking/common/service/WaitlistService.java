package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.yogi_yoga_class.BatchYogiEnrollDTO;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.exception.BatchEnrollmentFailedException;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.service.yogaClass.YogaClassBookingCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WaitlistService {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistService.class);

    private final YogaClassRepository yogaClassRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final YogaClassBookingCacheService yogaClassBookingCacheService;
    private final BatchYogiEnrollService batchYogiEnrollService;

    public WaitlistService(@Qualifier("longRedisTemplate") RedisTemplate<String, Long> redisTemplate,
                           YogaClassBookingCacheService yogaClassBookingCacheService,
                           BatchYogiEnrollService batchYogiEnrollService,
                           YogaClassRepository yogaClassRepository) {
        this.redisTemplate = redisTemplate;
        this.yogaClassBookingCacheService = yogaClassBookingCacheService;
        this.batchYogiEnrollService = batchYogiEnrollService;
        this.yogaClassRepository = yogaClassRepository;
    }

    private String getWaitlistKey(Long classId) {
        return "waitlist:class:" + classId;
    }

    public void addToWaitlist(Long classId, Long yogiId) {
        ListOperations<String, Long> listOps = redisTemplate.opsForList();
        listOps.rightPush(getWaitlistKey(classId), yogiId);
        logger.info("Yogi {} added to waitlist for class {}", yogiId, classId);
    }

    public List<Long> getWaitlist(Long classId) {
        ListOperations<String, Long> listOps = redisTemplate.opsForList();
        Long size = listOps.size(getWaitlistKey(classId));
        if (size != null && size > 0) {
            return listOps.range(getWaitlistKey(classId), 0, size - 1);
        }
        return List.of();
    }

    public Long removeFromWaitlist(Long classId) {
        ListOperations<String, Long> listOps = redisTemplate.opsForList();
        return listOps.leftPop(getWaitlistKey(classId));
    }

    public void promoteFromWaitlistIfAvailable(Long classId) {
        long currentBooked = yogaClassBookingCacheService.getCurrentBookingCount(classId);
        YogaClass yogaClass = yogaClassRepository.findById(classId).orElseThrow();
        long maxCapacity = yogaClass.getMaxNumberOfYogis();
        if (currentBooked < maxCapacity) {
            Long yogiIdToPromote = removeFromWaitlist(classId);
            if (yogiIdToPromote != null) {
                logger.info("Attempting to promote Yogi {} from waitlist for class {}", yogiIdToPromote, classId);
                // Create a DTO to simulate a single-yogi enrollment request
                BatchYogiEnrollDTO enrollRequest = new BatchYogiEnrollDTO();
                enrollRequest.setYogaClassId(classId);
                enrollRequest.setYogiIds(List.of(yogiIdToPromote));

                try {
                    // Attempt to enroll the yogi. This should handle package checks and credit deduction.
                    batchYogiEnrollService.enrollYogisInBatch(enrollRequest);
                    logger.info("Successfully promoted and enrolled Yogi {} from waitlist for class {}", yogiIdToPromote, classId);
                } catch (BatchEnrollmentFailedException e) {
                    logger.warn("Failed to enroll Yogi {} promoted from waitlist for class {}: {}",
                            yogiIdToPromote, classId, e.getMessage());
                    // Optionally, you might want to handle this failure (e.g., move the yogi back to the waitlist
                    // or notify someone). For simplicity, we'll just log it here.
                }
            } else {
                logger.info("Waitlist for class {} is empty, no one to promote.", classId);
            }
        } else {
            logger.warn("Could not retrieve max capacity for class {}, cannot promote from waitlist.", classId);
        }
    }
}
