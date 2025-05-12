package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageCreateDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageUpdateDTO;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.entity.YogiPackage;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogiPackageMapper;
import com.yogiBooking.common.repository.YogiPackageRepository;
import com.yogiBooking.common.repository.YogiRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YogiPackageService {

    private final YogiPackageRepository yogiPackageRepository;
    private final YogiPackageMapper yogiPackageMapper;
    private final YogiRepository yogiRepository;
    private final PaymentService paymentService;
    private final GenericSearchService searchService;
    private final EntityManager entityManager;

    @Transactional
    public List<YogiPackageDTO> getAllYogiPackages() {
        List<YogiPackage> yogiPackages = yogiPackageRepository.findAll();
        return yogiPackages.stream()
                .map(yogiPackageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public YogiPackageDTO getYogiPackageById(Long id) {
        YogiPackage yogiPackage = yogiPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi package with ID %d not found.".formatted(id)));
        return yogiPackageMapper.toDTO(yogiPackage);
    }

    @Transactional
    public YogiPackageDTO createYogiPackage(YogiPackageCreateDTO yogiPackageCreateDTO) {
        Yogi yogi = yogiRepository.findById(yogiPackageCreateDTO.getYogiId())
                .orElseThrow(() -> new ResourceNotFoundException("Yogi with ID %d not found.".formatted(yogiPackageCreateDTO.getYogiId())));

        PaymentResult paymentResult = paymentService.handlePaymentAndVerification(yogiPackageCreateDTO, yogi, yogiPackageMapper);

        YogiPackage savedYogiPackage = paymentResult.savedYogiPackage();
        entityManager.refresh(savedYogiPackage);
        return yogiPackageMapper.toDTO(savedYogiPackage);
    }

    @Transactional
    public YogiPackageDTO updateYogiPackage(Long id, YogiPackageUpdateDTO yogiPackageUpdateDTO) {
        YogiPackage yogiPackage = yogiPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi package with ID %d not found.".formatted(id)));

        yogiRepository.findById(yogiPackageUpdateDTO.getYogiId())
                .orElseThrow(() -> new ResourceNotFoundException("Yogi with ID %d not found.".formatted(yogiPackageUpdateDTO.getYogiId())));

        YogiPackage yogiPackageUpdate = yogiPackageMapper.toEntity(yogiPackageUpdateDTO);
        BeanUtils.copyProperties(yogiPackageUpdate, yogiPackage);

        entityManager.refresh(yogiPackageUpdate);
        return yogiPackageMapper.toDTO(yogiPackageUpdate);
    }

    @Transactional
    public void deleteYogiPackage(Long id) {
        YogiPackage yogiPackage = yogiPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi package with ID %d not found.".formatted(id)));
        yogiPackageRepository.delete(yogiPackage);
    }


    @Transactional
    public Page<YogiPackageDTO> findYogiPackageByFilter(SearchRequest request, Pageable pageable) {
        Page<YogiPackage> yogiPackages = searchService.search(request, yogiPackageRepository, pageable);
        return yogiPackages.map(yogiPackageMapper::toDTO);
    }
}