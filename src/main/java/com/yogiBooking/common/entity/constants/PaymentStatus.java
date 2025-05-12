package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus implements BaseEnum<Integer>{
    SUCCESS(1),
    INSUFFICENT(2),
    TRANSACTION_CANCLE(3);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}

