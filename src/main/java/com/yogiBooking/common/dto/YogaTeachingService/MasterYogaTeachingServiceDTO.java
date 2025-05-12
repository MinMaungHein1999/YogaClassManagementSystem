package com.yogiBooking.common.dto.YogaTeachingService;

import com.yogiBooking.common.entity.constants.Status;
import lombok.Data;

@Data
public class MasterYogaTeachingServiceDTO {
    private String name;
    private String code;
    private String description;
    private Long serviceCategoryId;
    private Status status;
}
