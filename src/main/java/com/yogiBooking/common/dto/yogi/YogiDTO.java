package com.yogiBooking.common.dto.yogi;

import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.entity.constants.Rating;
import com.yogiBooking.common.entity.constants.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class YogiDTO extends MasterYogiDTO {
    private long id;
    private String nrc;
    private Status status;
    private String levelName;
    private String countryName;
    private String regionName;
    private String cityName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private Long updatedById;
    private UserDTO loginUserDTO;
    private YogiNrcDTO yogiNrcDTO;
}
