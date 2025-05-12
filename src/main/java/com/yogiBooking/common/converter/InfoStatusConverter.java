package com.yogiBooking.common.converter;

import com.yogiBooking.common.entity.constants.InfoStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InfoStatusConverter extends BaseEnumConverter<InfoStatus, Integer> {
    public InfoStatusConverter(){
        super(InfoStatus.class);
    }
}
