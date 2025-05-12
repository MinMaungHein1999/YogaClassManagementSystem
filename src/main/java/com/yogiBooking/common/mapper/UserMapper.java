package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings(
            @Mapping(source = "role.id", target = "role")
    )
    UserDTO toDTO(User user);

    @Mappings(
            @Mapping(source = "roleId", target = "roleId")
    )
    UserCreateDTO toCreateDTO(User user);

    @Mappings(
            @Mapping(source = "role", target = "role.id")
    )
    User toEntity(UserDTO userDTO);
}
