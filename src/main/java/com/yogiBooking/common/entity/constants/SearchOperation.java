package com.yogiBooking.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchOperation implements BaseEnum<String> {
    CONTAINS("cn"),
    EQUALS("eq"),
    BEGINS_WITH("bw"),
    ENDS_WITH("ew"),
    GREATER_THAN_EQUALS("ge"),
    LESS_THAN_EQUALS("le");

    private final String keyword;

    @Override
    public String getValue() {
        return this.keyword;
    }

}
