package com.yogiBooking.common.converter;

import com.yogiBooking.common.entity.constants.ClassStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClassStatusConverter extends BaseEnumConverter<ClassStatus, Integer> {
    public ClassStatusConverter() {
        super(ClassStatus.class);
    }
}

