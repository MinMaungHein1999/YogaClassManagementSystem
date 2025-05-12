package com.yogiBooking.common.dto.yogi_yoga_class;

import lombok.Data;
import java.util.List;

@Data
public class BatchCancelEnrollDTO {
    private Long yogaClassId;
    private List<Long> yogiIds;
}
