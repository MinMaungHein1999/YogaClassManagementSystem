package com.yogiBooking.common.service.yogaClass;

import com.yogiBooking.common.controller.LevelController;
import com.yogiBooking.common.dto.yoga_class.YogaClassCreateDTO;
import com.yogiBooking.common.dto.yoga_class.YogaClassDTO;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yoga_class.YogaClassUpdateDTO;
import com.yogiBooking.common.dto.yogi.YogiDTO;
import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogaClassMapper;
import com.yogiBooking.common.mapper.YogiMapper;
import com.yogiBooking.common.repository.*;
import com.yogiBooking.common.service.GenericSearchService;
import com.yogiBooking.common.service.PackageCreditService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class YogaClassService {
    private static final Logger logger = LoggerFactory.getLogger(YogaClassService.class);
    private final YogaClassBookingCacheService yogaClassBookingCacheService;
    private final YogaClassRepository yogaClassRepository;
    private final LevelRepository levelRepository;
    private final YogiRepository yogiRepository;
    private final YogiMapper yogiMapper;
    private final CountryRepository countryRepository;
    private final PackageCreditService packageCreditService;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final YogiYogaClassRepository yogiYogaClassRepository;
    private final YogaTeachingServiceRepository yogaTeachingServiceRepository;
    private final YogaClassMapper yogaClassMapper;
    private final GenericSearchService genericSearchService;
    private final EntityManager entityManager;

    @Transactional
    public YogaClassDTO saveNewYogaClass(YogaClassCreateDTO dto) {
        validateServiceAndTeachingExistence(dto.getServiceCategoryId(), dto.getYogaTeachingServiceId());

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level with id %d not found".formatted(dto.getLevelId())));

        YogaClass yogaClass = yogaClassMapper.toEntity(dto);
        yogaClass.setLevel(level);
        YogaClass savedClass = yogaClassRepository.save(yogaClass);
        entityManager.refresh(savedClass);

        return yogaClassMapper.toDTO(savedClass);
    }

    @Transactional(readOnly = true)
    public List<YogaClassDTO> getAllYogaClasses() {
        return yogaClassRepository.findAll().stream().map(yogaClassMapper::toDTO).toList();
    }

    @Transactional
    public void deleteYogaClass(Long yogaClassId) {
        if (!yogaClassRepository.existsById(yogaClassId)) {
            throw new ResourceNotFoundException("Yoga class with id %d not found".formatted(yogaClassId));
        }
        yogaClassRepository.deleteById(yogaClassId);
    }

    @Transactional
    public void refundCreditsForWaitingList(Long classId) {
        YogaClass yogaClass = yogaClassRepository.findById(classId).orElseThrow(() -> new IllegalArgumentException("Class not found"));
        if(yogaClass.getEndDate().isAfter(LocalDate.now()))
            return;

        List<YogiDTO> yogiDTOS = this.getWaitingYogiListByClassId(classId);


        for (YogiDTO yogiDTO : yogiDTOS) {
            Long yogiId = yogiDTO.getId();
            try {
                packageCreditService.refundCredit(yogiId, classId);
                // Remove the entry after successful refund
                yogiYogaClassRepository.deleteByYogiIdAndClassId(yogiId, classId);
                logger.info("Refunded credit to Yogi {} for class {}", yogiId, classId);

            } catch (Exception e) {
                logger.error("Failed to refund credit to Yogi {} for class {}. Error: {}", yogiId, classId, e.getMessage());
                // Consider adding a retry mechanism or storing failed refunds for manual processing
            }
        }
    }

    @Transactional
    public YogaClassDTO updateExistingYogaClass(Long id, YogaClassUpdateDTO dto) {
        YogaClass yogaClass = yogaClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga class with id %d not found".formatted(id)));

        validateServiceAndTeachingExistence(dto.getServiceCategoryId(), dto.getYogaTeachingServiceId());

        yogaClass.setTitle(dto.getTitle());
        yogaClass.setStartDate(dto.getStartDate());
        yogaClass.setEndDate(dto.getEndDate());
        yogaClass.setClassType(dto.getClassType());

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level with id %d not found".formatted(dto.getLevelId())));
        yogaClass.setLevel(level);

        ServiceCategory serviceCategory = serviceCategoryRepository.findById(dto.getServiceCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category with id %d not found".formatted(dto.getServiceCategoryId())));
        yogaClass.setServiceCategory(serviceCategory);

        YogaTeachingService yogaTeachingService = yogaTeachingServiceRepository.findById(dto.getYogaTeachingServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Teaching Service with id %d not found".formatted(dto.getYogaTeachingServiceId())));
        yogaClass.setYogaTeachingService(yogaTeachingService);

        yogaClassRepository.flush();
        entityManager.refresh(yogaClass);
        return yogaClassMapper.toDTO(yogaClass);
    }

    @Transactional(readOnly = true)
    public Page<YogaClassDTO> findYogaClassesByFilters(SearchRequest request, Pageable pageable) {
        Page<YogaClass> yogaClasses = genericSearchService.search(request, yogaClassRepository, pageable);
        return yogaClasses.map(yogaClassMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public YogaClassDTO findYogaClassById(long id) {
        YogaClass yogaClass = yogaClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga class with id %d not found".formatted(id)));
        return yogaClassMapper.toDTO(yogaClass);
    }

    private void validateServiceAndTeachingExistence(Long serviceCategoryId, Long yogaTeachingServiceId) {
        if (!serviceCategoryRepository.existsById(serviceCategoryId)) {
            throw new ResourceNotFoundException("Service Category with ID %d not found".formatted(serviceCategoryId));
        }
        if (!yogaTeachingServiceRepository.existsById(yogaTeachingServiceId)) {
            throw new ResourceNotFoundException("Yoga Teaching Service with ID %d not found".formatted(yogaTeachingServiceId));
        }
    }

    public List<YogiDTO> getWaitingYogiListByClassId(Long classId) {
        List<String> waitlist = yogaClassBookingCacheService.getWaitlist(classId);
        if (waitlist == null || waitlist.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> yogiIds = waitlist.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Yogi> yogis = yogiRepository.findByIdIn(yogiIds); // Corrected method name
        return yogis.stream()
                .map(yogiMapper::toDto)
                .collect(Collectors.toList());
    }
}
