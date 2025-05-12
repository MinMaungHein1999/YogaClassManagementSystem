package com.yogiBooking.common.service.yogaClass;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.entity.constants.ClassStatus;
import com.yogiBooking.common.entity.constants.JoinedStatus;
import com.yogiBooking.common.entity.constants.PackageStatus;
import com.yogiBooking.common.entity.constants.PaymentStatus;
import com.yogiBooking.common.exception.BatchEnrollmentFailedException;
import com.yogiBooking.common.exception.BusinessValidationException;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogiYogaClassMapper;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.repository.YogiPackageRepository;
import com.yogiBooking.common.repository.YogiYogaClassRepository;
import com.yogiBooking.common.repository.YogiRepository;
import com.yogiBooking.common.dto.yogi_yoga_class.*;
import com.yogiBooking.common.service.BatchCancelEnrollService;
import com.yogiBooking.common.service.BatchYogiEnrollService;
import com.yogiBooking.common.service.GenericSearchService;
import com.yogiBooking.common.service.PackageCreditService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassBookingService {
    private static final Logger logger = LoggerFactory.getLogger(ClassBookingService.class);
    private final YogiYogaClassMapper yogiYogaClassMapper;
    private final YogiYogaClassRepository yogiYogaClassRepository;
    private final EntityManager entityManager;
    private final LockService lockService;
    private final BatchCancelEnrollService batchCancelEnrollService;
    private final BatchYogiEnrollService batchYogiEnrollService;
    private final YogiRepository yogiRepository;
    private final YogaClassRepository yogaClassRepository;
    private final GenericSearchService searchService;

    @Transactional(readOnly = true)
    public List<YogiYogaClassResponseDTO> getAllYogiYogaClasses() {
        return yogiYogaClassRepository.findAll()
                .stream()
                .map(yogiYogaClassMapper::toDTO)
                .toList();
    }

    public BatchUpdateEnrollDTO enrollUpdateYogisInBatch(BatchUpdateEnrollDTO batchUpdateEnrollDTO) {
        Long classId = batchUpdateEnrollDTO.getYogaClassId();
        String lockKey = "lock:yoga_class:" + classId;

        List<String> errorMessages = new ArrayList<>();
        List<Long> successfullyEnrolledYogiIds = new ArrayList<>();
        List<YogiYogaClassResponseDTO> enrollmentResponses = new ArrayList<>();

        // Validate class exists
        yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Yoga Class with ID %d not found".formatted(classId)));

        try {
            lockService.lock(lockKey, 5);

            for (Long yogiId : batchUpdateEnrollDTO.getYogiIds()) {
                try {
                    Yogi yogi = yogiRepository.findById(yogiId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Yogi with ID %d not found".formatted(yogiId)));

                    List<YogiYogaClass> existingClasses = yogiYogaClassRepository
                            .findByYogiIdAndClassId(yogiId, classId)
                            .orElse(Collections.emptyList());

                    if (existingClasses.isEmpty()) {
                        errorMessages.add("No enrollment found for Yogi ID %d and Class ID %d".formatted(yogiId, classId));
                        continue;
                    }

                    YogiYogaClassUpdateDTO updateDTO = yogiYogaClassMapper.toDTO(batchUpdateEnrollDTO);
                    updateDTO.setYogiId(yogi.getId());

                    for (YogiYogaClass existing : existingClasses) {
                        YogiYogaClassResponseDTO updated = updateYogiYogaClass(existing.getId(), updateDTO);
                        enrollmentResponses.add(updated);
                        successfullyEnrolledYogiIds.add(yogiId);
                    }
                } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
                    errorMessages.add(e.getMessage());
                } catch (Exception e) {
                    errorMessages.add("Unexpected error for Yogi ID %d: %s".formatted(yogiId, e.getMessage()));
                }
            }

        } finally {
            lockService.unlock(lockKey);
        }

        if (!errorMessages.isEmpty()) {
            throw new ResourceAlreadyExistsException("Some updates failed: " + String.join(", ", errorMessages));
        }

        batchUpdateEnrollDTO.setYogiIds(successfullyEnrolledYogiIds);
        return batchUpdateEnrollDTO;
    }

    public BatchYogiEnrollDTO processBatchEnrollment(BatchYogiEnrollDTO batchEnrollRequest) {
        logger.info("Processing batch enrollment for class {}", batchEnrollRequest.getYogaClassId());
        return batchYogiEnrollService.enrollYogisInBatch(batchEnrollRequest);
    }

    public void processBatchCancellation(BatchCancelEnrollDTO batchCancelRequest) {
        logger.info("Processing batch cancellation for class {}", batchCancelRequest.getYogaClassId());
        batchCancelEnrollService.cancelEnrollYogisInBatch(batchCancelRequest);
    }

    @Transactional(readOnly = true)
    public YogiYogaClassResponseDTO getYogiYogaClassById(Long id) {
        return yogiYogaClassRepository.findById(id)
                .map(yogiYogaClassMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi Yoga class with ID %d not found"
                        .formatted(id)));
    }

    @Transactional
    public YogiYogaClassResponseDTO updateYogiYogaClass(Long id, YogiYogaClassUpdateDTO updateDTO) {
        YogiYogaClass yogiYogaClass = yogiYogaClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi Yoga class with ID %d not found"
                        .formatted(id)));
        yogiYogaClass.getYogaClass().setId(updateDTO.getYogaClassId());
        yogiYogaClass.getYogi().setId(updateDTO.getYogiId());
        yogiYogaClass.setJoinedDate(updateDTO.getJoinedDate());
        yogiYogaClass.setStatus(updateDTO.getStatus());
        yogiYogaClass.setJoinedStatus(updateDTO.getJoinedStatus());
        yogiYogaClass.setRemark(updateDTO.getRemark());
        yogiYogaClass.setRating(updateDTO.getRating());

        yogiYogaClassRepository.save(yogiYogaClass);
        yogiYogaClassRepository.flush();
        entityManager.refresh(yogiYogaClass);
        return yogiYogaClassMapper.toDTO(yogiYogaClass);
    }

    @Transactional
    public Boolean deleteYogiYogaClass(Long id) {
        if (!yogiYogaClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Yogi Yoga class with ID %d not found".formatted(id));
        }
        yogiYogaClassRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public LocalDate getJoinDate(Long yogiId, Long classId) {
        return yogiYogaClassRepository.findJoinedDateByYogiIdAndClassId(yogiId, classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Yogi with ID %d is not found for class with ID %d.".formatted(yogiId, classId)
                ));
    }

    @Transactional(readOnly = true)
    public Page<YogiYogaClassResponseDTO> findYogiYogaClassByFilter(SearchRequest request, Pageable pageable){
        Page<YogiYogaClass> yogiYogaClasses = searchService.search(request, yogiYogaClassRepository, pageable);
        return yogiYogaClasses.map(yogiYogaClassMapper::toDTO);
    }

}

