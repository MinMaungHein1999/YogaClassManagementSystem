package com.yogiBooking.common.dto.service_category;

import com.yogiBooking.common.entity.constants.Status;
import lombok.Data;

@Data
public class MasterServiceCategoryDTO {
    private String name;
    private String code;
    private boolean isUnderControl;
    private Status status;
}
