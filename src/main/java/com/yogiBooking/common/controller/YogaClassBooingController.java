package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.service.BatchYogiEnrollService;
import com.yogiBooking.common.service.yogaClass.ClassBookingService;
import com.yogiBooking.common.utils.JsonUtil;
import com.yogiBooking.common.dto.yogi_yoga_class.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Yogi Yoga Class Management", description = "APIs for managing Yogi Yoga Classes")
@APIResource(apiPath = "/yogi-yoga-classes")
@RequiredArgsConstructor
@Slf4j
public class YogaClassBooingController {

    private final ClassBookingService classBookingService;
    private final BatchYogiEnrollService batchYogiEnrollService;

    private static final Logger logger = LoggerFactory.getLogger(YogaClassBooingController.class);

    @Operation(summary = "Retrieve All Yogi Yoga Classes", description = "Retrieve all yogi yoga classes APIs")
    @ApiResponse(responseCode = "200", description = "Retrieve all yogi yoga classes",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = YogiYogaClassResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<YogiYogaClassResponseDTO>> getAllYogiYogaClasses() {
        log.info("Request to retrieve all yogi yoga classes received.");
        var classes = classBookingService.getAllYogiYogaClasses();
        log.info("Yogi yoga classes retrieved: {}", JsonUtil.toPrettyJson(classes));
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Batch Enrolled Update Yogis to Yoga Class", description = "Enroll multiple yogis Update by processing a batch of IDs")
    @PostMapping("/update-yogis-batch")
    public ResponseEntity<BatchUpdateEnrollDTO> enrollYogisUpdateInBatch(
            @RequestBody BatchUpdateEnrollDTO requestDTO) {
        var responseDTO = classBookingService.enrollUpdateYogisInBatch(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Batch Enroll Yogis to Yoga Class", description = "Enroll multiple yogis by processing a batch of IDs")
    @PostMapping("/joinBatch")
    public ResponseEntity<BatchYogiEnrollDTO> joinYogisInBatch(@RequestBody BatchYogiEnrollDTO batchEnrollRequest) {
        var response = classBookingService.processBatchEnrollment(batchEnrollRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Batch Cancel Enrollments of Yogis in Yoga Class", description = "Cancel enrollment for multiple yogis by processing a batch of IDs.")
    @PostMapping("/unjoinBatch")
    public ResponseEntity<Void> unjoinYogisInBatch(@RequestBody BatchCancelEnrollDTO batchCancelEnrollDTO) {
        classBookingService.processBatchCancellation(batchCancelEnrollDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieve Yogi Yoga Class By Id", description = "Retrieve yogi yoga class by id API")
    @ApiResponse(responseCode = "200", description = "Retrieve yogi yoga class by id",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YogiYogaClassResponseDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<YogiYogaClassResponseDTO> getYogiYogaClassById(@PathVariable Long id) {
        log.info("Request to retrieve yogi yoga class with ID: {}", id);
        var yogaClass = classBookingService.getYogiYogaClassById(id);
        if (yogaClass != null) {
            log.info("Yogi yoga class retrieved: {}", JsonUtil.toPrettyJson(yogaClass));
        } else {
            log.warn("Yogi yoga class with ID {} not found.", id);
        }
        return ResponseEntity.ok(yogaClass);
    }

    @Operation(summary = "Create Yogi Yoga Class", description = "Create a new yogi yoga class API")
    @ApiResponse(responseCode = "201", description = "Yogi yoga class created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YogiYogaClassResponseDTO.class)))
    @PostMapping
    public ResponseEntity<YogiYogaClassResponseDTO> createYogiYogaClass(@RequestBody YogiYogaClassCreateDTO createDTO) {
        log.info("Request to create yogi yoga class: {}", JsonUtil.toPrettyJson(createDTO));
        var created = batchYogiEnrollService.createYogiYogaClass(createDTO);
        log.info("Yogi yoga class created: {}", JsonUtil.toPrettyJson(created));
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update Yogi Yoga Class", description = "Update an existing yogi yoga class API")
    @ApiResponse(responseCode = "200", description = "Yogi yoga class updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YogiYogaClassResponseDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<YogiYogaClassResponseDTO> updateYogiYogaClass(
            @PathVariable Long id,
            @RequestBody YogiYogaClassUpdateDTO updateDTO
    ) {
        log.info("Request to update yogi yoga class with ID: {}", id);
        var updated = classBookingService.updateYogiYogaClass(id, updateDTO);
        if (updated != null) {
            log.info("Yogi yoga class updated: {}", JsonUtil.toPrettyJson(updated));
        } else {
            log.warn("Yogi yoga class with ID {} not found for update.", id);
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete Yogi Yoga Class", description = "Delete a yogi yoga class API")
    @ApiResponse(responseCode = "200", description = "Yogi yoga class deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteYogiYogaClass(@PathVariable Long id) {
        log.info("Request to delete yogi yoga class with ID: {}", id);
        boolean deleted = classBookingService.deleteYogiYogaClass(id);
        if (deleted) {
            log.info("Yogi yoga class with ID {} deleted successfully.", id);
        } else {
            log.warn("Yogi yoga class with ID {} not found for deletion.", id);
        }
        return ResponseEntity.ok(deleted);
    }

    @Operation(summary = "Retrieve Yogi Join Date By Class", description = "Retrieve the join date of a yogi for a specific yoga class.")
    @GetMapping("/{yogiId}/class/{classId}/join-date")
    public ResponseEntity<LocalDate> getJoinDate(@PathVariable Long yogiId, @PathVariable Long classId) {
        log.info("Request to retrieve join date for Yogi ID: {} in Class ID: {}", yogiId, classId);
        LocalDate joinDate = classBookingService.getJoinDate(yogiId, classId);
        log.info("Yogi with ID {} joined class with ID {} on: {}", yogiId, classId, joinDate);
        return ResponseEntity.ok(joinDate);
    }

    @Operation(summary = "Search Yogi Yoga Classes by Filter", description = "Search for yogi yoga classes with filtering and pagination")
    @PostMapping("/search")
    public ResponseEntity<Page<YogiYogaClassResponseDTO>> findYogiYogaClassByFilter(
            @RequestBody SearchRequest request,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<YogiYogaClassResponseDTO> result = classBookingService.findYogiYogaClassByFilter(request, pageable);

        if (result.isEmpty()) {
            logger.info("No yogi yoga class found for the given filter criteria");
        }
        return ResponseEntity.ok(result);
    }
}

