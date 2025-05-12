package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessingStatus implements BaseEnum<Integer>{
    WAITING(1),
    RUNNING(2),
    IMPORTED(3),
    FAILED(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
