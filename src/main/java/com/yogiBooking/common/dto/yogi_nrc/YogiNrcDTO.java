package com.yogiBooking.common.dto.yogi_nrc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiNrcDTO extends MasterYogiNrcDTO{
    private Long id;
    private String nrc;
    private String nrcEn;
    private String nrcCodeNameMm;
    private String nrcCodeNameEn;
    private String nrcCodeDetails;
    private String nrcCodePrefixCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private Long updatedById;
}
