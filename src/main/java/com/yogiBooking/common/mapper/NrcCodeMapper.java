package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.nrc_codes.NrcCodeCreateDto;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeDTO;
import com.yogiBooking.common.entity.NrcCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface NrcCodeMapper {
    NrcCodeMapper INSTANCE = Mappers.getMapper(NrcCodeMapper.class);

    NrcCode toEntity(NrcCodeCreateDto nrcCodeCreateDto);

    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    NrcCode toEntity(NrcCodeDTO nrcCodeDTO);

    @Mappings({
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById")
    })
    NrcCodeDTO toDto(NrcCode nrcCode);

}
