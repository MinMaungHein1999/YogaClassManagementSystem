package com.yogiBooking.common.mapper;

import com.yogiBooking.common.dto.yogi_nrc.MasterYogiNrcDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcCreateDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.entity.YogiNrc;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface YogiNrcMapper {
    YogiNrcMapper INSTANCE = Mappers.getMapper(YogiNrcMapper.class);

    @Mappings({
            @Mapping(source = "nrcCodeId", target = "nrcCodeId"),
            @Mapping(source = "yogiId", target = "yogiId")
    })
    YogiNrcCreateDTO toDto(MasterYogiNrcDTO masterYogiNrcDTO);

    @Mappings({
            @Mapping(source = "yogiId", target = "yogi.id"),
            @Mapping(source = "nrcCodeId", target = "nrcCode.id")
    })
    YogiNrc toEntity(YogiNrcCreateDTO yogiNrcCreateDTO);

    @Mappings({
            @Mapping(source = "yogiId", target = "yogi.id"),
            @Mapping(source = "nrcCodeId", target = "nrcCode.id")
    })
    YogiNrc toEntity(MasterYogiNrcDTO masterYogiNrcDTO);

    @InheritConfiguration(name = "toEntity")
    @Mappings({
            @Mapping(source = "createdById", target = "createdBy.id"),
            @Mapping(source = "updatedById", target = "updatedBy.id")
    })
    YogiNrc toEntity(YogiNrcDTO yogiNrcDTO);

    @Mappings({
            @Mapping(source = "nrcCode.id", target = "nrcCodeId"),
            @Mapping(source = "yogi.id", target = "yogiId"),
            @Mapping(source = "nrcCode.prefixCode", target = "nrcCodePrefixCode"),
            @Mapping(source = "nrcCode.nameMm", target = "nrcCodeNameMm"),
            @Mapping(source = "nrcCode.nameEn", target = "nrcCodeNameEn"),
            @Mapping(source = "nrcCode.details", target = "nrcCodeDetails"),
            @Mapping(source = "createdBy.id", target = "createdById"),
            @Mapping(source = "updatedBy.id", target = "updatedById")
    })
    YogiNrcDTO toDto(YogiNrc yogiNrc);
}
