package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.NrcType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NrcTypeConverter extends BaseEnumConverter<NrcType, String> {

    public NrcTypeConverter() {
        super(NrcType.class);
    }
}
