package com.yogiBooking.common.dto.yogi_yoga_class;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiYogaClassUpdateDTO extends MasterYogiYogaClassDTO {
    private Long yogiId;
}
