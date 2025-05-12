package com.yogiBooking.common.utils;


import com.yogiBooking.common.dto.yoga_class.YogaClassCreateDTO;
import com.yogiBooking.common.dto.yoga_class.YogaClassDTO;
import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.entity.constants.ClassStatus;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import java.time.LocalDate;


@Component
public class ClassStatusUtil {

    @Named("calculateStatusFromEntity")
    public static ClassStatus calculateStatus(YogaClass yogaClass) {
        return determineStatus(yogaClass.getStartDate(), yogaClass.getEndDate());
    }

    @Named("calculateStatusFromDTO")
    public static ClassStatus calculateStatus(YogaClassDTO yogaClass) {
        return determineStatus(yogaClass.getStartDate(), yogaClass.getEndDate());
    }


    @Named("calculateStatusFromCreateDTO")
    public static ClassStatus calculateStatus(YogaClassCreateDTO yogaClass) {
        return determineStatus(yogaClass.getStartDate(), yogaClass.getEndDate());
    }

    private static ClassStatus determineStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(startDate)) {
            return ClassStatus.UPCOMMING;
        } else if (!today.isAfter(endDate)) {
            return ClassStatus.ONGOING;
        } else {
            return ClassStatus.FINISHED;
        }
    }
}
