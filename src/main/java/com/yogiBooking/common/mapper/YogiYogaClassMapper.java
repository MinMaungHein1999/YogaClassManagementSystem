package com.yogiBooking.common.mapper;

import com.yogiBooking.common.entity.YogiYogaClass;
import com.yogiBooking.common.dto.yogi_yoga_class.*;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface YogiYogaClassMapper {
    YogiYogaClassMapper INSTANCE = Mappers.getMapper(YogiYogaClassMapper.class);

    @Mappings({
            @Mapping(source = "yogaClassId", target = "yogaClass.id"),
            @Mapping(source = "yogiId", target = "yogi.id"),
    })
    YogiYogaClass toEntity(YogiYogaClassCreateDTO yogiYogaClassCreateDTO);

    @InheritConfiguration(name = "toEntity")
    @Mappings({
            @Mapping(source = "updatedById", target = "updatedBy.id"),
            @Mapping(source = "createdById", target = "createdBy.id")
    })
    YogiYogaClass toEntity(YogiYogaClassResponseDTO yogiYogaClassResponseDTO);

    @Mappings({
            @Mapping(source = "updatedBy.id", target = "updatedById"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "yogi.id", target = "yogiId"),
            @Mapping(source = "yogi.yogiId", target = "yogiBookId"),
            @Mapping(source = "yogi.name", target = "name"),
            @Mapping(source = "yogi.level.name", target = "levelName"),
            @Mapping(source = "yogi.nrc", target = "nrc"),
            @Mapping(source = "yogi.passportID", target = "passportId"),
            @Mapping(source = "yogi.phone", target = "phone"),
            @Mapping(source = "yogaClass.id", target = "yogaClassId")
    })
    YogiYogaClassResponseDTO toDTO(YogiYogaClass yogiYogaClass);

    YogiYogaClassCreateDTO toDTO(BatchYogiEnrollDTO batchYogiEnrollDTO);
    YogiYogaClassUpdateDTO toDTO(BatchUpdateEnrollDTO batchUpdateEnrollDTO);

}
