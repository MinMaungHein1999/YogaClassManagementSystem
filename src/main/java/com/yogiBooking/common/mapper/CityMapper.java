package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.city.CityCreateDTO;
import com.yogiBooking.common.dto.city.CityDTO;
import com.yogiBooking.common.entity.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface CityMapper {
    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    @Mappings({
            @Mapping(source = "regionId", target = "region.id")
    })
    City toEntity(CityCreateDTO cityCreateDTO);

    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id"),
    })
    City toEntity(CityDTO cityDTO);

    @Mappings({
            @Mapping(source = "region.id", target = "regionId"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById")
    })
    CityDTO toDto(City city);

}
