package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dashboard.YogiStatisticsDto;
import com.yogiBooking.common.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Dashboard", description = "Admin Dashboard APIs")
@RestController
@RequiredArgsConstructor
@APIResource(apiPath = "/api/dashboard")
public class DashboardController {

    @Autowired
    private final StatisticsService statisticsService;

    @Operation(
            summary = "Retrieve dashboard statistics",
            description = "Fetch aggregated Yogi statistics data for the system dashboard"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Yogi statistics",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiStatisticsDto.class)))
    })
    @GetMapping("/statistics")
    public ResponseEntity<YogiStatisticsDto> getDashboardStatistics() {
        YogiStatisticsDto statistics = statisticsService.getYogiStaticsService();
        return ResponseEntity.ok(statistics);
    }
}
