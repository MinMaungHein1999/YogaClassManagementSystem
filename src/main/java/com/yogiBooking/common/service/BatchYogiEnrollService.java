package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.yogi_yoga_class.BatchYogiEnrollDTO;
import com.yogiBooking.common.dto.yogi_yoga_class.YogiYogaClassCreateDTO;
import com.yogiBooking.common.dto.yogi_yoga_class.YogiYogaClassResponseDTO;
import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.entity.constants.JoinedStatus;
import com.yogiBooking.common.entity.constants.PackageStatus;
import com.yogiBooking.common.entity.constants.PaymentStatus;
import com.yogiBooking.common.exception.BatchEnrollmentFailedException;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogiYogaClassMapper;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.repository.YogiPackageRepository;
import com.yogiBooking.common.repository.YogiRepository;
import com.yogiBooking.common.repository.YogiYogaClassRepository;
import com.yogiBooking.common.service.yogaClass.LockService;
import com.yogiBooking.common.service.yogaClass.YogaClassBookingCacheService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BatchYogiEnrollService {

    private static final Logger logger = LoggerFactory.getLogger(BatchYogiEnrollService.class);

    private final YogaClassRepository yogaClassRepository;
    private final YogiRepository yogiRepository;
    private final YogiPackageRepository yogiPackageRepository;
    private final YogiYogaClassRepository yogiYogaClassRepository;
    private final YogiYogaClassMapper yogiYogaClassMapper;
    private final EntityManager entityManager;
    private final LockService lockService;
    private final YogaClassBookingCacheService yogaClassBookingCacheService;

    public BatchYogiEnrollService(YogaClassRepository yogaClassRepository, YogiRepository yogiRepository,
                                  YogiPackageRepository yogiPackageRepository, YogiYogaClassRepository yogiYogaClassRepository,
                                  YogiYogaClassMapper yogiYogaClassMapper, EntityManager entityManager, LockService lockService,
                                  YogaClassBookingCacheService yogaClassBookingCacheService) {
        this.yogaClassRepository = yogaClassRepository;
        this.yogiRepository = yogiRepository;
        this.yogiPackageRepository = yogiPackageRepository;
        this.yogiYogaClassRepository = yogiYogaClassRepository;
        this.yogiYogaClassMapper = yogiYogaClassMapper;
        this.entityManager = entityManager;
        this.lockService = lockService;
        this.yogaClassBookingCacheService = yogaClassBookingCacheService;
    }

    public BatchYogiEnrollDTO enrollYogisInBatch(BatchYogiEnrollDTO batchEnrollRequest) {
        Long classId = batchEnrollRequest.getYogaClassId();
        YogaClass yogaClass = findYogaClassOrThrow(classId);
        Long maxCapacity = yogaClass.getMaxNumberOfYogis();
        List<Long> yogiIdsToEnroll = batchEnrollRequest.getYogiIds();
        List<Long> enrolledYogiIds = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        Map<Long, Yogi> yogiMap = findYogisOrThrow(yogiIdsToEnroll);

        String lockKey = "lock:yoga_class:" + classId;
        try {
            lockService.lock(lockKey, 10);
            long currentCount = yogaClassBookingCacheService.getCurrentBookingCount(classId);

            for (Long yogiId : yogiIdsToEnroll) {
                Yogi yogi = yogiMap.get(yogiId);
                if (currentCount >= maxCapacity) {
                    handleWaitlist(classId, yogiId);
                    continue;
                }

                try {
                    enrollYogi(yogaClass, yogi, batchEnrollRequest);
                    enrolledYogiIds.add(yogiId);
                    currentCount++;
                } catch (EnrollmentException e) {
                    errorMessages.add("Yogi %d could not enroll: %s".formatted(yogiId, e.getMessage()));
                }
            }
        } finally {
            lockService.unlock(lockKey);
        }

        if (!errorMessages.isEmpty()) {
            throw new BatchEnrollmentFailedException("Some yogis failed to enroll.", errorMessages);
        }

        batchEnrollRequest.setYogiIds(enrolledYogiIds);
        return batchEnrollRequest;
    }

    private YogaClass findYogaClassOrThrow(Long classId) {
        return yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Class with ID %d not found".formatted(classId)));
    }

    private Map<Long, Yogi> findYogisOrThrow(List<Long> yogiIds) {
        List<Yogi> yogis = yogiRepository.findAllById(yogiIds);
        if (yogis.size() != yogiIds.size()) {
            throw new ResourceNotFoundException("One or more Yogi IDs not found.");
        }
        return yogis.stream().collect(Collectors.toMap(Yogi::getId, yogi -> yogi));
    }

    private void handleWaitlist(Long classId, Long yogiId) {
        yogaClassBookingCacheService.pushToWaitlist(classId, yogiId);
        logger.info("Yogi {} added to waitlist for class {}", yogiId, classId);
    }

    private void enrollYogi(YogaClass yogaClass, Yogi yogi, BatchYogiEnrollDTO batchEnrollRequest) {
        Country country = yogaClass.getCountry();
        ServiceCategory serviceCategory = yogaClass.getServiceCategory();
        List<YogiPackage> yogiPackages = yogiPackageRepository.findActiveByCountryIdAndYogiIdAndServiceCategoryIdAndPackageStatus(
                country.getId(), yogi.getId(), serviceCategory.getId(), PackageStatus.ACTIVE);

        if (yogiPackages.isEmpty()) {
            cancelEnrollment(yogi, batchEnrollRequest, "No active package for '%s' (%s) in %s."
                    .formatted(serviceCategory.getName(), country.getName(), country.getName()));
            throw new EnrollmentException("No active package found.");
        }

        YogiPackage yogiPackage = yogiPackages.getFirst();
        if (yogiPackage.getCredit() < yogaClass.getFeeOfCredit()) {
            cancelEnrollment(yogi, batchEnrollRequest, "Insufficient credit: Package '%s' has only %.2f credits, but class requires %.2f credits."
                    .formatted(serviceCategory.getName(), yogiPackage.getCredit(), yogaClass.getFeeOfCredit()), PaymentStatus.INSUFFICENT);
            throw new EnrollmentException("Insufficient credit.");
        }

        // Decrement credit and create enrollment
        yogiPackage.setCredit(yogiPackage.getCredit() - yogaClass.getFeeOfCredit());
        yogiPackageRepository.save(yogiPackage);

        YogiYogaClassCreateDTO createDTO = yogiYogaClassMapper.toDTO(batchEnrollRequest);
        createDTO.setYogiId(yogi.getId());
        createDTO.setJoinedStatus(JoinedStatus.JOINING);
        this.createYogiYogaClass(createDTO);
        yogaClassBookingCacheService.incrementBookingCount(yogaClass.getId());
        logger.info("Yogi {} successfully enrolled in class {}", yogi.getId(), yogaClass.getId());
    }

    private void cancelEnrollment(Yogi yogi, BatchYogiEnrollDTO batchEnrollRequest, String reason, PaymentStatus paymentStatus) {
        YogiYogaClassCreateDTO createDTO = yogiYogaClassMapper.toDTO(batchEnrollRequest);
        createDTO.setYogiId(yogi.getId());
        createDTO.setJoinedStatus(JoinedStatus.CANCEL);
        createDTO.setPaymentStatus(paymentStatus);
        createDTO.setRemark(reason);
        this.createYogiYogaClass(createDTO);
    }

    private void cancelEnrollment(Yogi yogi, BatchYogiEnrollDTO batchEnrollRequest, String reason) {
        cancelEnrollment(yogi, batchEnrollRequest, reason, PaymentStatus.TRANSACTION_CANCLE);
    }

    public YogiYogaClassResponseDTO createYogiYogaClass(YogiYogaClassCreateDTO createDTO) {
        Long yogiId = createDTO.getYogiId();
        Long classId = createDTO.getYogaClassId();

        Yogi yogi = yogiRepository.findById(yogiId)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi with ID %d not found".formatted(yogiId)));

        yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Class with ID %d not found".formatted(classId)));

        boolean alreadyJoined = yogiYogaClassRepository
                .findByYogiIdAndClassId(yogiId, classId)
                .map(list -> !list.isEmpty())
                .orElse(false);

        if (alreadyJoined) {
            throw new ResourceAlreadyExistsException(
                    "Yogi with ID %d has already joined Yoga Class with ID %d".formatted(yogiId, classId)
            );
        }

        YogiYogaClass yogiYogaClass = yogiYogaClassMapper.toEntity(createDTO);
        yogiYogaClass.setYogi(yogi);

        YogiYogaClass saved = yogiYogaClassRepository.save(yogiYogaClass);
        entityManager.refresh(saved);

        return yogiYogaClassMapper.toDTO(saved);
    }

    private static class EnrollmentException extends RuntimeException {
        public EnrollmentException(String message) {
            super(message);
        }
    }
}