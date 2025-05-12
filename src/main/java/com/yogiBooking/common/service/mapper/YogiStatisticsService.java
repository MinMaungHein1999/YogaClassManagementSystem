package com.yogiBooking.common.service.mapper;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.dto.CountProjection;
import com.yogiBooking.common.repository.YogiYogaClassRepository;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class YogiStatisticsService {

    @Autowired
    private YogiYogaClassRepository yogiYogaClassRepository;

    @Named("getCountByGender")
    public List<CountProjection> getCountByGender(YogaClass yogaClass) {
        return yogiYogaClassRepository.countByGender(yogaClass.getId());
    }

    @Named("getCountByLevel")
    public List<CountProjection> getCountByLevel(YogaClass yogaClass) {
        return yogiYogaClassRepository.countByLevel(yogaClass.getId());
    }

    @Named("getCountByJoinedStatus")
    public List<CountProjection> getCountByJoinedStatus(YogaClass yogaClass) {
        return yogiYogaClassRepository.countByJoinedStatus(yogaClass.getId());
    }

    @Named("getCountByRating")
    public List<CountProjection> getCountByRating(YogaClass yogaClass) {
        return yogiYogaClassRepository.countByRating(yogaClass.getId());
    }
}
