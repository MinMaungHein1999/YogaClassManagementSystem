package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PackageStatus implements BaseEnum<Integer>{
    ACTIVE(1),
    EXPIRED(2),
    PAYMENT_FAIL(3),
    USED_UP(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
