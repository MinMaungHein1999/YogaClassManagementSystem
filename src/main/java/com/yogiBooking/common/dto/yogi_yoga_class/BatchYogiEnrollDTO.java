package com.yogiBooking.common.dto.yogi_yoga_class;
import lombok.Data;

import java.util.List;

@Data
public class BatchYogiEnrollDTO extends MasterYogiYogaClassDTO {
    private List<Long> yogiIds;
}
