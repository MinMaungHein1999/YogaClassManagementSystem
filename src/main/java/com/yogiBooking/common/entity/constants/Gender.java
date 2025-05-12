package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Gender implements BaseEnum<Integer>{
    MONK(1, "ရဟန်း"),
    NUN(2, "သီလရှင်"),
    NOVICE(3, "သာမဏေ"),
    MALE(4, "ကျား"),
    FEMALE(5, "မ"),
    OTHER(6, "အခြား");

    private final int value;
    private final String mmName;

    @Override
    public Integer getValue() {
        return this.value;
    }

}
