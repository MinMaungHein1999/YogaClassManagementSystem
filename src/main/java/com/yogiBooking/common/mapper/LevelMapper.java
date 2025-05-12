package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.level.LevelCreateDTO;
import com.yogiBooking.common.dto.level.LevelDTO;
import com.yogiBooking.common.entity.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface LevelMapper {

    LevelMapper INSTANCE = Mappers.getMapper(LevelMapper.class);

    Level toEntity(LevelCreateDTO levelCreateDTO);

    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    Level toEntity(LevelDTO levelDTO);

    @Mappings({
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById")
    })
    LevelDTO toDTO(Level level);

}
