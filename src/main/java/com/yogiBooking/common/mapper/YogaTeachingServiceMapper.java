package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceCreateDTO;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceDTO;
import com.yogiBooking.common.entity.YogaTeachingService;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface YogaTeachingServiceMapper {
    YogaTeachingServiceMapper INSTANCE = Mappers.getMapper(YogaTeachingServiceMapper.class);

    @Mappings({
            @Mapping(source = "serviceCategoryId", target = "serviceCategory.id")
    })
    YogaTeachingService toEntity(YogaTeachingServiceCreateDTO serviceCategoryCreateDTO);

    @InheritConfiguration
    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    YogaTeachingService toEntity(YogaTeachingServiceDTO serviceCategoryDTO);

    @Mappings({
            @Mapping(source = "serviceCategory.id", target = "serviceCategoryId"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById")
    })
    YogaTeachingServiceDTO toDto(YogaTeachingService yogaTeachingService);
}
