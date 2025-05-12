package com.yogiBooking.common.dto;

import lombok.Data;

@Data
public class SearchFilter {
    private String term;
    private String value;
    private String matchType;
    private String type;
}
