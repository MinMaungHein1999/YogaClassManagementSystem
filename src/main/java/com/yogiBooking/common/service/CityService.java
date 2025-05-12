package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.city.CityCreateDTO;
import com.yogiBooking.common.dto.city.CityDTO;
import com.yogiBooking.common.dto.city.CityUpdateDTO;
import com.yogiBooking.common.entity.City;
import com.yogiBooking.common.entity.Region;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.CityMapper;
import com.yogiBooking.common.repository.CityRepository;
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
public class CityService {

    private final GenericSearchService genericSearchService;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final EntityManager entityManager;
    private final RegionRepository regionRepository;

    @Transactional
    public CityDTO createCity(CityCreateDTO cityCreateDTO){
        City city = cityMapper.toEntity(cityCreateDTO);
        City savedCity = cityRepository.save(city);
        entityManager.refresh(savedCity);
        return cityMapper.toDto(savedCity);
    }

    @Transactional
    public CityDTO updateCity(Long id, CityUpdateDTO cityUpdateDTO){
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found!!"));
        city.setNameEn(cityUpdateDTO.getNameEn());
        city.setNameMm(cityUpdateDTO.getNameMm());
        Region region = regionRepository.findById(cityUpdateDTO.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region with Id %d not found!"
                        .formatted(cityUpdateDTO.getRegionId())));
        city.setRegion(region);
        City updatedCity = cityRepository.save(city);
        cityRepository.flush();
        entityManager.refresh(updatedCity);
        return cityMapper.toDto(updatedCity);
    }

    @Transactional
    public void deleteCity(Long id){
        if(!cityRepository.existsById(id)){
            throw new ResourceNotFoundException("City with id "+id+" not found");
        }
        cityRepository.deleteById(id);
    }

    @Transactional
    public Optional<CityDTO> findById(Long id){
        CityDTO cityDTO = cityMapper.toDto(cityRepository.findById(id).orElseThrow(() -> new RuntimeException("City not found!!")));
        return Optional.ofNullable(cityDTO);
    }

    @Transactional
    public List<CityDTO> getAllCities(){
        return cityRepository.findAll().stream().map(cityMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Page<CityDTO> findCityByFilter(SearchRequest request, Pageable pageable){
        Page<City> cities = genericSearchService.search(request,cityRepository,pageable);
        return cities.map(cityMapper::toDto);
    }
}