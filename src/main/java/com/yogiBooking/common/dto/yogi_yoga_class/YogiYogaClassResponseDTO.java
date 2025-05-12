package com.yogiBooking.common.dto.yogi_yoga_class;

import com.yogiBooking.common.entity.constants.PaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiYogaClassResponseDTO extends MasterYogiYogaClassDTO {
    private Long id;
    private Long yogiId;
    private String name;
    private String nrc;
    private String passportId;
    private String yogiBookId;
    private String fatherName;
    private String phone;
    private String levelName;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Long createdById;
    private Long updatedById;
}
