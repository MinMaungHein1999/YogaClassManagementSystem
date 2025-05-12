package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Rating implements BaseEnum<Integer>{
    EXCELLENT(1, "excellent"),
    GOOD(2, "good"),
    AVERAGE(3, "average"),
    BELOW_AVERAGE(4, "below average"),
    POOR(5, "poor"),;
    private final int value;
    private final String name;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
