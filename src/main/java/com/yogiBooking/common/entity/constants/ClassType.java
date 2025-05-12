package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClassType implements BaseEnum<Integer>{
    ONLINE(0),
    ONSITE(1);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
