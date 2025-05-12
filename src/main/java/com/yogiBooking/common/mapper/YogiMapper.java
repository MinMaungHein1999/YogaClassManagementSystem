package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.yogi.YogiCreateDTO;
import com.yogiBooking.common.dto.yogi.YogiDTO;
import com.yogiBooking.common.dto.yogi.YogiUpdateDTO;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.service.mapper.ToYogiNrcDTOMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ToYogiNrcDTOMapper.class})
public interface YogiMapper {

    YogiMapper INSTANCE = Mappers.getMapper(YogiMapper.class);

    @Mappings({
            @Mapping(source = "regionId", target = "region.id"),
            @Mapping(source = "cityId", target = "city.id"),
            @Mapping(source = "levelId", target = "level.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "name", target = "loginUser.name"),
            @Mapping(source = "email", target = "loginUser.email"),
            @Mapping(source = "password", target = "loginUser.password"),
            @Mapping(source = "roleId", target = "loginUser.roleId")
    })
    Yogi toEntity(YogiCreateDTO yogiCreateDTO);

    @Mappings({
            @Mapping(source = "regionId", target = "region.id"),
            @Mapping(source = "cityId", target = "city.id"),
            @Mapping(source = "levelId", target = "level.id"),
            @Mapping(source = "countryId", target = "country.id"),
            @Mapping(source = "name", target = "loginUser.name"),
            @Mapping(source = "email", target = "loginUser.email"),
            @Mapping(source = "password", target = "loginUser.password"),
            @Mapping(source = "roleId", target = "loginUser.roleId")
    })
    Yogi toEntity(YogiUpdateDTO yogiUpdateDTO);

    @InheritConfiguration(name = "toEntity")
    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    Yogi toEntity(YogiDTO yogiDTO);

    @Mappings({
            @Mapping(source = "region.id", target = "regionId"),
            @Mapping(source = "region.nameMm", target = "regionName"),
            @Mapping(source = "country.id", target = "countryId"),
            @Mapping(source = "country.name", target = "countryName"),
            @Mapping(source = "city.id", target = "cityId"),
            @Mapping(source = "city.nameMm", target = "cityName"),
            @Mapping(source = "level.id", target = "levelId"),
            @Mapping(source = "level.name", target= "levelName"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById"),
            @Mapping(source = "yogi", target = "yogiNrcDTO", qualifiedByName = "convertYogiToNrcDTO"),
            @Mapping(source = "loginUser.id", target = "loginUserDTO.id"),
            @Mapping(source = "loginUser.name", target = "loginUserDTO.name"),
            @Mapping(source = "loginUser.email", target = "loginUserDTO.email"),
            @Mapping(source = "loginUser.role.id", target = "loginUserDTO.role")
    })
    YogiDTO toDto(Yogi yogi);
}
