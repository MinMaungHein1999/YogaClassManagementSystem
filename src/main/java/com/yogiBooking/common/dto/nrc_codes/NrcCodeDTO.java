package com.yogiBooking.common.dto.nrc_codes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NrcCodeDTO extends MasterNrcCodeDto{
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long updatedById;
    private Long createdById;
}

