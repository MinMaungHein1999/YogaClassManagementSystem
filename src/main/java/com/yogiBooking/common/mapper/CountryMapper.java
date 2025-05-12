package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.country.CountryCreateDTO;
import com.yogiBooking.common.dto.country.CountryDTO;
import com.yogiBooking.common.entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    Country toEntity(CountryCreateDTO cityCreateDTO);


    Country toEntity(CountryDTO countryDTO);

    CountryDTO toDto(Country country);

}
