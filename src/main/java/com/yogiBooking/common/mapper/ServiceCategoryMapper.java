package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.service_category.ServiceCategoryDTO;
import com.yogiBooking.common.dto.service_category.ServiceCategoryCreateDTO;
import com.yogiBooking.common.entity.ServiceCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper {
    ServiceCategoryMapper INSTANCE = Mappers.getMapper(ServiceCategoryMapper.class);

    ServiceCategory toEntity(ServiceCategoryCreateDTO serviceCategoryCreateDTO);

    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    ServiceCategory toEntity(ServiceCategoryDTO serviceCategoryDTO);

    @Mappings({
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById"),
    })
    ServiceCategoryDTO toDto(ServiceCategory serviceCategory);
}
