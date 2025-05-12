package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.yogi_yoga_class.BatchCancelEnrollDTO;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.repository.YogiRepository;
import com.yogiBooking.common.repository.YogiYogaClassRepository;
import com.yogiBooking.common.service.yogaClass.YogaClassBookingCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BatchCancelEnrollService {

    private static final Logger logger = LoggerFactory.getLogger(BatchCancelEnrollService.class);

    private final YogaClassRepository yogaClassRepository;
    private final YogiRepository yogiRepository;
    private final YogiYogaClassRepository yogiYogaClassRepository;
    private final YogaClassBookingCacheService yogaClassBookingCacheService;
    private final PackageCreditService packageCreditService;
    private final WaitlistService waitlistService;

    public BatchCancelEnrollService(YogaClassRepository yogaClassRepository, YogiRepository yogiRepository,
                                    YogiYogaClassRepository yogiYogaClassRepository,
                                    YogaClassBookingCacheService yogaClassBookingCacheService,
                                    PackageCreditService packageCreditService, WaitlistService waitlistService) {
        this.yogaClassRepository = yogaClassRepository;
        this.yogiRepository = yogiRepository;
        this.yogiYogaClassRepository = yogiYogaClassRepository;
        this.yogaClassBookingCacheService = yogaClassBookingCacheService;
        this.packageCreditService = packageCreditService;
        this.waitlistService = waitlistService;
    }

    public void cancelEnrollYogisInBatch(BatchCancelEnrollDTO dto) {
        Long classId = dto.getYogaClassId();
        List<Long> yogiIds = dto.getYogiIds();
        YogaClass yogaClass = findYogaClassOrThrow(classId);
        List<String> errorMessages = new ArrayList<>();

        for (Long yogiId : yogiIds) {
            try {
                cancelYogiEnrollment(yogaClass, yogiId);
            } catch (Exception ex) {
                logger.error("Failed to unenroll Yogi ID {} from class {}: {}", yogiId, classId, ex.getMessage());
                errorMessages.add("Failed to unenroll Yogi ID %d: %s".formatted(yogiId, ex.getMessage()));
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new BatchCancelEnrollmentFailedException("Batch unenrollment completed with errors.", errorMessages);
        }
    }

    private YogaClass findYogaClassOrThrow(Long classId) {
        return yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Class with ID %d not found".formatted(classId)));
    }

    private void cancelYogiEnrollment(YogaClass yogaClass, Long yogiId) {
        Yogi yogi = findYogiOrThrow(yogiId);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, yogaClass.getStartDate());
        boolean within4Hours = duration.toHours() <= 4;

        yogiYogaClassRepository.deleteByYogiIdAndClassId(yogiId, yogaClass.getId());
        yogaClassBookingCacheService.decrementBookingCount(yogaClass.getId());

        if (!within4Hours) {
            packageCreditService.refundCredit(yogiId, yogaClass.getId());
            logger.info("Refunded credit to yogiId: {} for classId: {}", yogiId, yogaClass.getId());
        } else {
            logger.info("Cancellation within 4 hours, no refund for yogiId: {}, classId: {}", yogiId, yogaClass.getId());
        }

        waitlistService.promoteFromWaitlistIfAvailable(yogaClass.getId());
    }

    private Yogi findYogiOrThrow(Long yogiId) {
        return yogiRepository.findById(yogiId)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi with ID %d not found".formatted(yogiId)));
    }

    public static class BatchCancelEnrollmentFailedException extends RuntimeException {
        private final List<String> errorMessages;

        public BatchCancelEnrollmentFailedException(String message, List<String> errorMessages) {
            super(message);
            this.errorMessages = errorMessages;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }
    }
}
