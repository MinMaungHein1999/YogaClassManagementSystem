package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceDTO;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceCreateDTO;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceUpdateDTO;
import com.yogiBooking.common.entity.ServiceCategory;
import com.yogiBooking.common.entity.YogaTeachingService;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogaTeachingServiceMapper;
import com.yogiBooking.common.repository.ServiceCategoryRepository;
import com.yogiBooking.common.repository.YogaTeachingServiceRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YogaTeachingServiceService {

    private final YogaTeachingServiceRepository yogaTeachingServiceRepository;
    private final YogaTeachingServiceMapper yogaTeachingServiceMapper;
    private final EntityManager entityManager;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final GenericSearchService searchService;

    @Transactional
    public List<YogaTeachingServiceDTO> getAllYogaTeachingServices() {
        List<YogaTeachingService> entities = yogaTeachingServiceRepository.findAll();
        return entities.stream()
                .map(yogaTeachingServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public YogaTeachingServiceDTO createYogaTeachingService(YogaTeachingServiceCreateDTO yogaTeachingServiceCreateDTO) {
        YogaTeachingService yogaTeachingService = yogaTeachingServiceMapper.toEntity(yogaTeachingServiceCreateDTO);
        YogaTeachingService savedYogaTeachingService = yogaTeachingServiceRepository.save(yogaTeachingService);
        entityManager.refresh(savedYogaTeachingService);
        return yogaTeachingServiceMapper.toDto(savedYogaTeachingService);
    }

    @Transactional
    public YogaTeachingServiceDTO updateYogaTeachingService(Long id, YogaTeachingServiceUpdateDTO yogaTeachingServiceUpdateDTO) {
        YogaTeachingService yogaTeachingService = yogaTeachingServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Teaching Service with ID %d not found.".formatted(id)));

        yogaTeachingService.setName(yogaTeachingServiceUpdateDTO.getName());
        yogaTeachingService.setCode(yogaTeachingServiceUpdateDTO.getCode());
        yogaTeachingService.setDescription(yogaTeachingServiceUpdateDTO.getDescription());
        yogaTeachingService.setStatus(yogaTeachingServiceUpdateDTO.getStatus());

        ServiceCategory serviceCategory = serviceCategoryRepository.findById(yogaTeachingServiceUpdateDTO.getServiceCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category with ID %d not found.".formatted(yogaTeachingServiceUpdateDTO.getServiceCategoryId())));

        yogaTeachingService.setServiceCategory(serviceCategory);
        YogaTeachingService updatedYogaTeachingService = yogaTeachingServiceRepository.save(yogaTeachingService);
        yogaTeachingServiceRepository.flush();
        entityManager.refresh(updatedYogaTeachingService);
        return yogaTeachingServiceMapper.toDto(updatedYogaTeachingService);
    }

    @Transactional
    public boolean deleteYogaTeachingServiceById(Long id) {
        YogaTeachingService yogaTeachingService = yogaTeachingServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Teaching Service with ID %d not found.".formatted(id)));
        yogaTeachingServiceRepository.delete(yogaTeachingService);
        return true;
    }

    @Transactional
    public YogaTeachingServiceDTO findYogaTeachingServiceById(Long id) {
        YogaTeachingService yogaTeachingService = yogaTeachingServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Teaching Service not found with ID: " + id));
        return yogaTeachingServiceMapper.toDto(yogaTeachingService);
    }

    @Transactional(readOnly = true)
    public List<YogaTeachingServiceDTO> findYogaTeachingServiceByServiceCategoryId(Long id) {
        serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with ID: " + id));
        List<YogaTeachingService> yogaTeachingServices = yogaTeachingServiceRepository.findByServiceCategoryId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yoga Teaching Services not found for Service Category ID: " + id));
        return yogaTeachingServices.stream()
                .map(yogaTeachingServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<YogaTeachingServiceDTO> findYogaTeachingServiceByFilter(SearchRequest request, Pageable pageable) {
        Page<YogaTeachingService> yogaTeachingServices = searchService.search(request, yogaTeachingServiceRepository, pageable);
        return yogaTeachingServices.map(yogaTeachingServiceMapper::toDto);
    }
}



