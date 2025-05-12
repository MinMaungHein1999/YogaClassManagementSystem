package com.yogiBooking.common.converter;

import com.yogiBooking.common.entity.constants.ClassType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClassTypeConverter extends BaseEnumConverter<ClassType, Integer> {
    public ClassTypeConverter() {
        super(ClassType.class);
    }
}
