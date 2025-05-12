package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.yoga_class.YogaClassCreateDTO;
import com.yogiBooking.common.dto.yoga_class.YogaClassDTO;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.service.mapper.YogiStatisticsService;
import com.yogiBooking.common.utils.ClassStatusUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {YogiStatisticsService.class, ClassStatusUtil.class})
public interface YogaClassMapper {

    YogaClassMapper INSTANCE = Mappers.getMapper(YogaClassMapper.class);

    @Mappings({
            @Mapping(source = "levelId", target = "level.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "yogaClassCreateDTO", target = "classStatus", qualifiedByName = "calculateStatusFromCreateDTO"),
            @Mapping(source = "yogaTeachingServiceId", target = "yogaTeachingService.id"),
            @Mapping(source = "serviceCategoryId", target = "serviceCategory.id")
    })
    YogaClass toEntity(YogaClassCreateDTO yogaClassCreateDTO);

    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "yogaClassDTO", target = "classStatus", qualifiedByName = "calculateStatusFromDTO"),
            @Mapping(source = "yogaTeachingServiceId", target = "yogaTeachingService.id"),
            @Mapping(source = "serviceCategoryId", target = "serviceCategory.id")
    })
    YogaClass toEntity(YogaClassDTO yogaClassDTO);

    @Mappings({
            @Mapping(source = "level.id", target = "levelId"),
            @Mapping(source = "yogaTeachingService.id", target = "yogaTeachingServiceId"),
            @Mapping(source = "serviceCategory.id", target = "serviceCategoryId"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "country.id", target = "countryId"),
            @Mapping(source = "yogaClass", target = "classStatus", qualifiedByName = "calculateStatusFromEntity"),
            @Mapping(source = "yogaClass", target = "countByGender", qualifiedByName = "getCountByGender"),
            @Mapping(source = "yogaClass", target = "countByLevel", qualifiedByName = "getCountByLevel"),
            @Mapping(source = "yogaClass", target = "countByJoinedStatus", qualifiedByName = "getCountByJoinedStatus"),
            @Mapping(source = "yogaClass", target = "countByRating", qualifiedByName = "getCountByRating")
    })
    YogaClassDTO toDTO(YogaClass yogaClass);
}

