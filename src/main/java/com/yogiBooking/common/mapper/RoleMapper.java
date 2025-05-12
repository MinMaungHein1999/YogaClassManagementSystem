package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.RoleDTO;
import com.yogiBooking.common.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDto(Role role);

    Role toEntity(RoleDTO roleDTO);
}

