package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.service_category.ServiceCategoryCreateDTO;
import com.yogiBooking.common.dto.service_category.ServiceCategoryDTO;
import com.yogiBooking.common.dto.service_category.ServiceCategoryUpdateDTO;
import com.yogiBooking.common.entity.ServiceCategory;
import com.yogiBooking.common.exception.BadRequestException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.ServiceCategoryMapper;
import com.yogiBooking.common.repository.ServiceCategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final GenericSearchService searchService;
    private final EntityManager entityManager;

    @Transactional
    public List<ServiceCategoryDTO> getAll() {
        List<ServiceCategory> serviceCategories = serviceCategoryRepository.findAll();
        return serviceCategories.stream()
                .map(serviceCategoryMapper::toDto)
                .toList();
    }

    @Transactional
    public ServiceCategoryDTO getById(Long id) {
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category with ID %d not found.".formatted(id)));
        return serviceCategoryMapper.toDto(serviceCategory);
    }

    @Transactional
    public ServiceCategoryDTO create(ServiceCategoryCreateDTO serviceCategoryCreateDTO) {
        ServiceCategory existingCategory = serviceCategoryRepository.findByName(serviceCategoryCreateDTO.getName());

        if (existingCategory != null) {
            throw new BadRequestException("Service category already exists!");
        }
        ServiceCategory serviceCategory = serviceCategoryMapper.toEntity(serviceCategoryCreateDTO);
        ServiceCategory savedServiceCategory = serviceCategoryRepository.save(serviceCategory);
        entityManager.refresh(savedServiceCategory);
        return serviceCategoryMapper.toDto(savedServiceCategory);
    }

    @Transactional
    public ServiceCategoryDTO update(Long id, ServiceCategoryUpdateDTO serviceCategoryUpdateDTO) {
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category with ID %d not found.".formatted(id)));

        serviceCategory.setName(serviceCategoryUpdateDTO.getName());
        serviceCategory.setCode(serviceCategoryUpdateDTO.getCode());
        serviceCategory.setStatus(serviceCategoryUpdateDTO.getStatus());
        ServiceCategory updatedServiceCategory = serviceCategoryRepository.save(serviceCategory);
        serviceCategoryRepository.flush();
        entityManager.refresh(updatedServiceCategory);
        return serviceCategoryMapper.toDto(updatedServiceCategory);
    }

    @Transactional
    public boolean delete(Long id) {
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category with ID %d not found.".formatted(id)));
        serviceCategoryRepository.delete(serviceCategory);
        return true;
    }

    @Transactional
    public Page<ServiceCategoryDTO> findServiceCategoryByFilter(SearchRequest request, Pageable pageable) {
        Page<ServiceCategory> serviceCategories = searchService.search(request, serviceCategoryRepository, pageable);
        return serviceCategories.map(serviceCategoryMapper::toDto);
    }
}
