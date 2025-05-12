package com.yogiBooking.common.scheduler;

import com.yogiBooking.common.entity.YogaClass;
import com.yogiBooking.common.repository.YogaClassRepository;
import com.yogiBooking.common.service.yogaClass.YogaClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyRefundScheduler {
    private final YogaClassService yogaClassService;
    private final YogaClassRepository yogaClassRepository;

    // Run this every day at 00:00
    @Scheduled(cron = "0 0 0 * * ?")
    public void processEndedYogaClasses() {
        LocalDate today = LocalDate.now();
        List<YogaClass> endedClasses = yogaClassRepository.findEndedYogaClasses(today);

        for (YogaClass yogaClass : endedClasses) {
            yogaClassService.refundCreditsForWaitingList(yogaClass.getId());
        }
    }
}
