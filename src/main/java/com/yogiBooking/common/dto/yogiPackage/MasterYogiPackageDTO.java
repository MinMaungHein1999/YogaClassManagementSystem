package com.yogiBooking.common.dto.yogiPackage;

import com.yogiBooking.common.entity.constants.PackageStatus;
import lombok.Data;

@Data
public class MasterYogiPackageDTO {
    private double credit;
    private double amountOfCredit;
    private String bankCardNumber;
    private Long yogiId;
    private Long countryId;
    private Long serviceCategoryId;
    private PackageStatus packageStatus;
}
