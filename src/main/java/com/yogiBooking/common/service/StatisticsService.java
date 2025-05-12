package com.yogiBooking.common.service;

import com.yogiBooking.common.dashboard.YogiStatisticsDto;
import com.yogiBooking.common.repository.YogiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    @Autowired
    private YogiRepository yogiRepository;

    public YogiStatisticsDto getYogiStaticsService() {
        YogiStatisticsDto yogiStatisticsDto = new YogiStatisticsDto();
        yogiStatisticsDto.setGenderCounts(this.yogiRepository.countByGender());
        yogiStatisticsDto.setLevelCounts(this.yogiRepository.countByLevel());
        yogiStatisticsDto.setCountByForeignStatus(this.yogiRepository.countByForeignStatus());

        return yogiStatisticsDto;
    }
}
