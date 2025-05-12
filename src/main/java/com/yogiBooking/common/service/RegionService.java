package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.region.RegionCreateDTO;
import com.yogiBooking.common.dto.region.RegionDTO;
import com.yogiBooking.common.dto.region.RegionUpdateDTO;
import com.yogiBooking.common.entity.Region;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.RegionMapper;
import com.yogiBooking.common.repository.RegionRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final GenericSearchService genericSearchService;
    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;
    private final EntityManager entityManager;

    @Transactional
    public RegionDTO createRegion(RegionCreateDTO regionCreateDTO){
        Region region = regionMapper.toEntity(regionCreateDTO);
        Region savedRegion = regionRepository.save(region);
        entityManager.refresh(savedRegion);
        return regionMapper.toDto(savedRegion);
    }

    @Transactional
    public RegionDTO updateRegion(Long id, RegionUpdateDTO regionUpdateDTO){
        Region region = regionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Region not found!!"));
        region.setNameEn(regionUpdateDTO.getNameEn());
        region.setNameMm(regionUpdateDTO.getNameMm());
        region.setCode(regionUpdateDTO.getCode());
        Region updatedRegion = regionRepository.save(region);
        regionRepository.flush();
        entityManager.refresh(updatedRegion);
        return regionMapper.toDto(updatedRegion);
    }

    @Transactional
    public void deleteRegion(Long id){
        regionRepository.deleteById(id);
    }

    @Transactional
    public Optional<RegionDTO> findById(Long id){
        RegionDTO regionDTO = regionMapper.toDto(this.regionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Region not found")));
        return Optional.ofNullable(regionDTO);
    }

    @Transactional
    public List<RegionDTO> getAllRegions(){
        return this.regionRepository.findAll().stream().map(regionMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Page<RegionDTO> findRegionByFilter(SearchRequest request, Pageable pageable){
        Page<Region> regions = genericSearchService.search(request,regionRepository,pageable);
        return regions.map(regionMapper::toDto);
    }

}
