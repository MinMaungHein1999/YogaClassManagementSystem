package com.yogiBooking.common.converter;

import com.yogiBooking.common.entity.constants.ProcessingStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProcessingStatusConverter extends BaseEnumConverter<ProcessingStatus, Integer> {
    public ProcessingStatusConverter() {
        super(ProcessingStatus.class);
    }
}
