package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.Gender;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter extends BaseEnumConverter<Gender, Integer> {

    public GenderConverter() {
        super(Gender.class);
    }
}
