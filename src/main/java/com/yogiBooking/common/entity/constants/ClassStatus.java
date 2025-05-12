package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClassStatus implements BaseEnum<Integer>{
    FINISHED(0),
    ONGOING(1),
    UPCOMMING(1);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
