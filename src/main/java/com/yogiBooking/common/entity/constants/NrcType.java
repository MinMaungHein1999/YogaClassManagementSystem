package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NrcType implements BaseEnum<String>{
    NAI("C", "နိုင်"),
    AEI("AC", "ဧည့်"),
    PYU("NC", "ပြု"),
    THA("M", "သ"),
    TEMPORARY("T", "ယာယီ"),
    SA("V", "စ"),
    THI("N", "သီ");

    private final String value;
    private final String mmName;

    public String getEnName(){
        return value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
