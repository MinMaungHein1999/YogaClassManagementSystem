package com.yogiBooking.common.dto.yoga_class;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yogiBooking.common.dto.CountProjection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonSerialize
public class YogaClassDTO extends MasterYogaClassDTO {
    private Long id;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Long createdById;
    private Long updatedById;
    private List<CountProjection> countByGender;
    private List<CountProjection> countByLevel;
    private List<CountProjection> countByJoinedStatus;
    private List<CountProjection> countByRating;

}

