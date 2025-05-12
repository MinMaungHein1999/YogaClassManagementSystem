package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JoinedStatus implements BaseEnum<Integer>{
    JOINING(0),
    PASS(1),
    WAITING(2),
    FAIL(3),
    CANCEL(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
