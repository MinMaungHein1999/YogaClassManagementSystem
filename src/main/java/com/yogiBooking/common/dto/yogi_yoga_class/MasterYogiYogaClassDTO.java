package com.yogiBooking.common.dto.yogi_yoga_class;

import com.yogiBooking.common.entity.constants.JoinedStatus;
import com.yogiBooking.common.entity.constants.PaymentStatus;
import com.yogiBooking.common.entity.constants.Rating;
import com.yogiBooking.common.entity.constants.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MasterYogiYogaClassDTO {
    private Long yogaClassId;
    private Status status = Status.ACTIVE;
    private JoinedStatus joinedStatus;
    private PaymentStatus paymentStatus;
    private LocalDate joinedDate;
    private Rating rating;
    private String remark;
}
