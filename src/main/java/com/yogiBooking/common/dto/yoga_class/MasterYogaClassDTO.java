package com.yogiBooking.common.dto.yoga_class;

import com.yogiBooking.common.entity.constants.ClassStatus;
import com.yogiBooking.common.entity.constants.ClassType;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MasterYogaClassDTO {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private ClassType classType;
    private Double feeOfCredit;
    private Double feeOfPrice;
    private Long maxNumberOfYogis;
    private String description;
    private ClassStatus classStatus;
    private Long countryId;
    private Long levelId;
    private Long yogaTeachingServiceId;
    private Long serviceCategoryId;
}
