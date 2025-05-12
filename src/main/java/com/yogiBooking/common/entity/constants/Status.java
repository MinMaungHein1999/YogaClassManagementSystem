package com.yogiBooking.common.entity.constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status implements BaseEnum<Integer>{
    ACTIVE(1),
    INACTIVE(3),
    DELETED(2);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
