package com.yogiBooking.common.service.mapper;

import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.service.YogiNrcService;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToYogiNrcDTOMapper {
    @Autowired
    private YogiNrcService yogiNrcService;

    @Named("convertYogiToNrcDTO")
    public YogiNrcDTO convertYogiToNrcDTO(Yogi yogi) {
        return yogiNrcService.getYogiNrcByYogiId(yogi.getId()).orElse(null);
    }
}
