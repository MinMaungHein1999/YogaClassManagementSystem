package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.JoinedStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JoinedStatusConverter extends BaseEnumConverter<JoinedStatus, Integer>{
    protected JoinedStatusConverter() {
        super(JoinedStatus.class);
    }
}
