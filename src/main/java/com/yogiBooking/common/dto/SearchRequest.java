package com.yogiBooking.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequest {
    private List<SearchFilter> filters;
}
