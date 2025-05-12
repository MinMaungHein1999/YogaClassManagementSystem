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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
    public BatchYogiEnrollDTO enrollYogisInBatch(BatchYogiEnrollDTO batchRequest) {
        Long classId = batchRequest.getYogaClassId();
        YogaClass yogaClass = findYogaClassOrThrow(classId);

        List<Long> yogiIdsToEnroll = batchRequest.getYogiIds();
        Map<Long, Yogi> yogiMap = findYogisOrThrow(yogiIdsToEnroll);

        List<Long> enrolledYogiIds = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        String lockKey = "lock:yoga_class:" + classId;

        try {
            lockService.lock(lockKey, 10);
            for (Long yogiId : yogiIdsToEnroll) {
                Yogi yogi = yogiMap.get(yogiId);
                try {
                    YogiYogaClassCreateDTO yogiYogaClassCreateDTO = new YogiYogaClassCreateDTO();

                    yogiYogaClassCreateDTO.setJoinedStatus(JoinedStatus.JOINING);
                    yogiYogaClassCreateDTO.setYogiId(yogi.getId());
                    yogiYogaClassCreateDTO.setYogaClassId(yogaClass.getId());
                    yogiYogaClassCreateDTO.setRating(batchRequest.getRating());
                    yogiYogaClassCreateDTO.setJoinedDate(batchRequest.getJoinedDate());
                    yogiYogaClassCreateDTO.setRemark(batchRequest.getRemark());

                    this.createYogiYogaClass(yogiYogaClassCreateDTO);
                    enrolledYogiIds.add(yogiId);
                } catch (Exception e) {
                    errorMessages.add("Yogi %d could not enroll: %s".formatted(yogiId, e.getMessage()));
                }
            }
        } finally {
            lockService.unlock(lockKey);
        }

        if (!errorMessages.isEmpty()) {
            throw new BatchEnrollmentFailedException("Some yogis failed to enroll.", errorMessages);
        }

        batchRequest.setYogiIds(enrolledYogiIds);
        return batchRequest;
    }

    private void waitlistYogi(YogiYogaClassCreateDTO createDTO, Long classId, Long yogiId) {
        handleWaitlist(classId, yogiId);
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

    private void cancelEnrollment(YogiYogaClassCreateDTO  createDTO) {
        yogiYogaClassRepository.save(yogiYogaClassMapper.toEntity(createDTO));
    }

    public YogiYogaClassResponseDTO createYogiYogaClass(YogiYogaClassCreateDTO createDTO) {
        Long yogiId = createDTO.getYogiId();
        Long classId = createDTO.getYogaClassId();

        // Validate entities
        Yogi yogi = getYogiOrThrow(yogiId);
        YogaClass yogaClass = getYogaClassOrThrow(classId);
        validateNoDuplicateEnrollment(yogiId, classId);

        // Validate and retrieve a valid package
        YogiPackage yogiPackage = getValidYogiPackageOrCancel(yogi, yogaClass, createDTO);

        // Deduct credit for active enrollment
        deductClassCredit(yogiPackage, yogaClass.getFeeOfCredit());

        long currentCount = yogaClassBookingCacheService.getCurrentBookingCount(classId);
        long maxCapacity = Optional.ofNullable(yogaClass.getMaxNumberOfYogis()).orElse(0L);

        // Determine if waitlisting is needed
        if (currentCount >= maxCapacity) {
            createDTO.setJoinedStatus(JoinedStatus.WAITING);
            createDTO.setPaymentStatus(PaymentStatus.SUCCESS);
            createDTO.setRemark("Yogi ID %d added to waiting list.".formatted(yogiId));
            logger.info("Yogi {} added to waitlist for class {}", yogiId, classId);
        } else {
            createDTO.setJoinedStatus(JoinedStatus.JOINING);
            createDTO.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        // Persist enrollment
        YogiYogaClass yogiYogaClass = yogiYogaClassMapper.toEntity(createDTO);
        yogiYogaClass.setYogi(yogi);
        YogiYogaClass saved = yogiYogaClassRepository.save(yogiYogaClass);

        // Update booking count if actively enrolled
        if (createDTO.getJoinedStatus() == JoinedStatus.JOINING) {
            yogaClassBookingCacheService.incrementBookingCount(classId);
            logger.info("Yogi {} successfully enrolled in class {}", yogiId, classId);
        }

        entityManager.refresh(saved);
        return yogiYogaClassMapper.toDTO(saved);
    }


    private Yogi getYogiOrThrow(Long yogiId) {
        return yogiRepository.findById(yogiId)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi with ID %d not found".formatted(yogiId)));
    }

    private YogaClass getYogaClassOrThrow(Long classId) {
        return yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Class with ID %d not found".formatted(classId)));
    }

    private void validateNoDuplicateEnrollment(Long yogiId, Long classId) {
        boolean alreadyJoined = yogiYogaClassRepository
                .findByYogiIdAndClassId(yogiId, classId)
                .map(list -> !list.isEmpty())
                .orElse(false);

        if (alreadyJoined) {
            throw new ResourceAlreadyExistsException("Yogi with ID %d has already joined Yoga Class with ID %d".formatted(yogiId, classId));
        }
    }

    private YogiPackage getValidYogiPackageOrCancel(Yogi yogi, YogaClass yogaClass, YogiYogaClassCreateDTO createDTO) {
        Country country = yogaClass.getCountry();
        ServiceCategory category = yogaClass.getServiceCategory();

        List<YogiPackage> packages = yogiPackageRepository.findActiveByCountryIdAndYogiIdAndServiceCategoryIdAndPackageStatus(
                country.getId(), yogi.getId(), category.getId(), PackageStatus.ACTIVE);

        if (packages.isEmpty()) {
            String reason = "No active package for '%s' (%s) in %s."
                    .formatted(category.getName(), country.getName(), country.getName());
            createDTO.setRemark(reason);
            createDTO.setJoinedStatus(JoinedStatus.CANCEL);
            createDTO.setPaymentStatus(PaymentStatus.TRANSACTION_CANCLE);
            cancelEnrollment(createDTO);
            throw new EnrollmentException(reason);
        }

        YogiPackage yogiPackage = packages.getFirst();

        if (yogiPackage.getCredit() < yogaClass.getFeeOfCredit()) {
            String reason = "Insufficient credit: Package '%s' has only %.2f credits, but class requires %.2f credits."
                            .formatted(category.getName(), yogiPackage.getCredit(), yogaClass.getFeeOfCredit());
            createDTO.setRemark(reason);
            createDTO.setJoinedStatus(JoinedStatus.CANCEL);
            createDTO.setPaymentStatus(PaymentStatus.INSUFFICENT);
            cancelEnrollment(createDTO);
            throw new EnrollmentException(reason);
        }

        return yogiPackage;
    }

    private void deductClassCredit(YogiPackage yogiPackage, double creditToDeduct) {
        yogiPackage.setCredit(yogiPackage.getCredit() - creditToDeduct);
        yogiPackageRepository.save(yogiPackage);
    }


    private static class EnrollmentException extends RuntimeException {
        public EnrollmentException(String message) {
            super(message);
        }
    }
}