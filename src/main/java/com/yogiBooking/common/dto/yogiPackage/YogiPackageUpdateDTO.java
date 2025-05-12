package com.yogiBooking.common.dto.yogiPackage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiPackageUpdateDTO extends MasterYogiPackageDTO{
    private Long id;
}
