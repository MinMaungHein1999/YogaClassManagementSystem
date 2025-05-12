package com.yogiBooking.common.dto.yogi;

import com.yogiBooking.common.entity.constants.NrcType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiUpdateDTO extends MasterYogiDTO{
    private String postFixDigit;
    private NrcType nrcType;

}
