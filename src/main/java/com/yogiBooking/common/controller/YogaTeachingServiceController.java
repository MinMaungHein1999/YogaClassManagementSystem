package com.yogiBooking.common.controller;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceCreateDTO;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceDTO;
import com.yogiBooking.common.dto.YogaTeachingService.YogaTeachingServiceUpdateDTO;
import com.yogiBooking.common.service.YogaTeachingServiceService;
import com.yogiBooking.common.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Yoga Teaching Service Management", description = "Yoga Teaching Service Management APIs")
@RestController
@RequestMapping("/yoga-teaching-services")
@RequiredArgsConstructor
public class YogaTeachingServiceController {

    private static final Logger logger = LoggerFactory.getLogger(YogaTeachingServiceController.class);
    private final YogaTeachingServiceService yogaTeachingServiceService;

    @Operation(summary = "Create Yoga Teaching Service", description = "Create Yoga Teaching Service API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Yoga Teaching Service successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = YogaTeachingServiceDTO.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<YogaTeachingServiceDTO> createYogaTeachingService(@RequestBody @Valid YogaTeachingServiceCreateDTO createDTO) {
        try {
            logger.info("Create Yoga Teaching Service request: {}", JsonUtil.toPrettyJson(createDTO));
            YogaTeachingServiceDTO yogaTeachingServiceDTO = yogaTeachingServiceService.createYogaTeachingService(createDTO);
            logger.info("Create Yoga Teaching Service response: {}", JsonUtil.toPrettyJson(yogaTeachingServiceDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(yogaTeachingServiceDTO);
        } catch (Exception e) {
            logger.error("Error creating Yoga Teaching Service: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Retrieve All Yoga Teaching Services", description = "Retrieve all Yoga Teaching Services API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieve all yoga teaching services",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = YogaTeachingServiceDTO.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<?> getAllYogaTeachingService() {
        try {
            logger.info("Retrieve All Yoga Teaching Services request");
            var yogaTeachingServiceDTOs = yogaTeachingServiceService.getAllYogaTeachingServices();
            logger.info("Retrieve All Yoga Teaching Services response: {}", JsonUtil.toPrettyJson(yogaTeachingServiceDTOs));
            return ResponseEntity.ok(yogaTeachingServiceDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving all yoga teaching services: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Yoga Teaching Service", description = "Delete Yoga Teaching Service API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Yoga Teaching Service successfully deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteYogaTeachingService(@PathVariable Long id) {
        try {
            logger.info("Delete yoga teaching service request with ID: {}", id);
            boolean deleteStatus = yogaTeachingServiceService.deleteYogaTeachingServiceById(id);
            logger.info("Delete yoga teaching service response: {}", deleteStatus);
            return ResponseEntity.ok(deleteStatus);
        } catch (Exception e) {
            logger.error("Error deleting yoga teaching service with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update Yoga Teaching Service", description = "Update Yoga Teaching Service API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Yoga teaching service successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = YogaTeachingServiceDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateYogaTeachingService(@PathVariable Long id, @RequestBody @Valid YogaTeachingServiceUpdateDTO updateDTO) {
        try {
            logger.info("Update yoga teaching service request with ID: {} and data: {}", id, JsonUtil.toPrettyJson(updateDTO));
            YogaTeachingServiceDTO yogaTeachingServiceDTO = yogaTeachingServiceService.updateYogaTeachingService(id, updateDTO);
            logger.info("Update yoga teaching service response: {}", JsonUtil.toPrettyJson(yogaTeachingServiceDTO));
            return ResponseEntity.ok(yogaTeachingServiceDTO);
        } catch (Exception e) {
            logger.error("Error updating yoga teaching service with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Find Yoga Teaching Service By ID", description = "Find Yoga Teaching Service By ID API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Find yoga teaching service by ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = YogaTeachingServiceDTO.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findYogaTeachingServiceById(@PathVariable Long id) {
        try {
            logger.info("Retrieve yoga teaching service by ID: {} request", id);
            YogaTeachingServiceDTO yogaTeachingServiceDTO = yogaTeachingServiceService.findYogaTeachingServiceById(id);
            logger.info("Retrieve yoga teaching service by ID: {} response: {}", id, JsonUtil.toPrettyJson(yogaTeachingServiceDTO));
            return ResponseEntity.ok(yogaTeachingServiceDTO);
        } catch (Exception e) {
            logger.error("Error retrieving yoga teaching service by ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Search Yoga Teaching Service by Filter", description = "Search for yoga teaching services with filtering and pagination")
    @PostMapping("/search")
    public ResponseEntity<Page<YogaTeachingServiceDTO>> findYogaTeachingServiceByFilter(@RequestBody SearchRequest request, @RequestParam int page, @RequestParam int size) {
        try {
            logger.info("Search Yoga Teaching Service request with filter: {}, page: {}, size: {}", JsonUtil.toPrettyJson(request), page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<YogaTeachingServiceDTO> yogaTeachingServicePage = yogaTeachingServiceService.findYogaTeachingServiceByFilter(request, pageable);
            if (yogaTeachingServicePage.isEmpty()) {
                logger.info("No yoga teaching services found for the given filter criteria (page {}, size {}).", page, size);
            } else {
                logger.info("Search Yoga Teaching Service response (page {}, size {}): {}", page, size, JsonUtil.toPrettyJson(yogaTeachingServicePage.getContent()));
            }
            return ResponseEntity.ok(yogaTeachingServicePage);
        } catch (Exception e) {
            logger.error("Error searching yoga teaching services (page {}, size {}): {}", page, size, e.getMessage());
            throw e;
        }
    }
}
