package com.yogiBooking.common.dto.nrc_codes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterNrcCodeDto {
    private String nameMm;
    private String nameEn;
    private Integer prefixCode;
    private String details;
}
