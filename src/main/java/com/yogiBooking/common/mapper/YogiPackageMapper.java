package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageCreateDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageUpdateDTO;
import com.yogiBooking.common.entity.User;
import com.yogiBooking.common.entity.YogiPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface YogiPackageMapper {

    YogiPackageMapper INSTANCE = Mappers.getMapper(YogiPackageMapper.class);

    @Mappings({
            @Mapping(source = "serviceCategory.id", target = "serviceCategoryId"),
            @Mapping(source = "country.id", target = "countryId"),
            @Mapping(source = "yogi.id", target = "yogiId"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById"),
    })
    YogiPackageDTO toDTO(YogiPackage yogiPackage);

    @Mappings({
            @Mapping(source = "serviceCategoryId", target = "serviceCategory.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "yogiId", target = "yogi.id"),
    })
    YogiPackage toEntity(YogiPackageCreateDTO yogiPackageCreateDTO);


    @Mappings({
            @Mapping(source = "serviceCategoryId", target = "serviceCategory.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "yogiId", target = "yogi.id"),
    })
    YogiPackage toEntity(YogiPackageUpdateDTO yogiPackageCreateDTO);

}
