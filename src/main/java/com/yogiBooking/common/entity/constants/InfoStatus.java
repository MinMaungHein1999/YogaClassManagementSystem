package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InfoStatus implements BaseEnum<Integer>{
    INVALID(1),
    VALID(2);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
