package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.country.CountryCreateDTO;
import com.yogiBooking.common.dto.country.CountryDTO;
import com.yogiBooking.common.dto.country.CountryUpdateDTO;
import com.yogiBooking.common.entity.Country;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.CountryMapper;
import com.yogiBooking.common.repository.CountryRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final GenericSearchService genericSearchService;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final EntityManager entityManager;

    @Transactional
    public CountryDTO createCountry(CountryCreateDTO countryCreateDTO){
        Country country = countryMapper.toEntity(countryCreateDTO);
        Country savedCountry = countryRepository.save(country);
        entityManager.refresh(savedCountry);
        return countryMapper.toDto(savedCountry);
    }

    @Transactional
    public CountryDTO updateCountry(Long id, CountryUpdateDTO countryUpdateDTO){
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found!!"));
        country.setName(countryUpdateDTO.getName());
        Country updatedCountry = countryRepository.save(country);
        countryRepository.flush();
        entityManager.refresh(updatedCountry);
        return countryMapper.toDto(updatedCountry);
    }

    @Transactional
    public void deleteCountry(Long id){
        if(!countryRepository.existsById(id)){
            throw new ResourceNotFoundException("Country with id " + id + " not found");
        }
        countryRepository.deleteById(id);
    }

    @Transactional
    public Optional<CountryDTO> findById(Long id){
        CountryDTO countryDTO = countryMapper.toDto(
                countryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Country not found!!")));
        return Optional.ofNullable(countryDTO);
    }

    @Transactional
    public List<CountryDTO> getAllCountries(){
        return countryRepository.findAll().stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }
}

