package com.yogiBooking.common.dashboard;

import com.yogiBooking.common.dto.CountProjection;
import lombok.Data;
import java.util.List;
@Data
public class YogiStatisticsDto {

    private List<CountProjection> genderCounts;
    private List<CountProjection> levelCounts;
    private List<CountProjection> countByForeignStatus;
}
