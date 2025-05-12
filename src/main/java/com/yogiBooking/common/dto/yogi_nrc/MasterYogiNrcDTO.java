package com.yogiBooking.common.dto.yogi_nrc;

import com.yogiBooking.common.entity.constants.NrcType;
import lombok.Data;

@Data
public class MasterYogiNrcDTO {
    private String postFixDigit;
    private NrcType type;
    private Long nrcCodeId;
    private Long yogiId;
}
