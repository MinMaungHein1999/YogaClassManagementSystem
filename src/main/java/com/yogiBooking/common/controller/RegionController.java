package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.region.RegionCreateDTO;
import com.yogiBooking.common.dto.region.RegionDTO;
import com.yogiBooking.common.dto.region.RegionUpdateDTO;
import com.yogiBooking.common.service.RegionService;
import com.yogiBooking.common.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Tag(name = "Region Management", description = "Region Management APIs")
@APIResource(apiPath = "/regions")
@RequiredArgsConstructor
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Operation(summary = "Create Region Class", description = "Create a new region")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Region created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class)))
    })
    @PostMapping
    public ResponseEntity<RegionDTO> createRegion(@RequestBody RegionCreateDTO regionCreateDTO){
        try {
            logger.info("Creating Region");
            RegionDTO regionDTO = regionService.createRegion(regionCreateDTO);
            logger.info("Created Region: {}", JsonUtil.toPrettyJson(regionDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(regionDTO);
        } catch (Exception e) {
            logger.error("Error creating Region: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating Region", e);
        }
    }

    @Operation(summary = "Update Region", description = "Update an existing region")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> updateRegion(@PathVariable Long id, @RequestBody RegionUpdateDTO regionUpdateDTO){
        try {
            logger.info("Updating Region with ID: {}", id);
            RegionDTO regionDTO = regionService.updateRegion(id, regionUpdateDTO);
            logger.info("Updated Region: {}", JsonUtil.toPrettyJson(regionDTO));
            return ResponseEntity.ok(regionDTO);
        } catch (Exception e) {
            logger.error("Error updating Region: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Region", description = "Delete a region")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        logger.info("Deleting Region with ID: {}", id);
        if (regionService.findById(id).isEmpty()) {
            logger.warn("Region with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with ID " + id + " not found");
        }
        regionService.deleteRegion(id);
        logger.info("Region with ID {} deleted successfully", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieve a region by ID", description = "Fetch a region by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the region",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Region not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RegionDTO> findRegionById(@PathVariable Long id) {
        logger.info("Finding Region by ID: {}", id);
        Optional<RegionDTO> regionOptional = regionService.findById(id);
        if (regionOptional.isEmpty()) {
            logger.warn("Region with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region with ID " + id + " not found");
        }
        RegionDTO region = regionOptional.get();
        logger.info("Retrieved Region: {}", JsonUtil.toPrettyJson(region));
        return ResponseEntity.ok(region);
    }

    @Operation(summary = "Get all Regions", description = "Retrieve all regions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Regions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegionDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        logger.info("Retrieving all Regions");
        List<RegionDTO> regions = regionService.getAllRegions();
        logger.info("Retrieved Regions: {}", JsonUtil.toPrettyJson(regions));
        return ResponseEntity.ok(regions);
    }

    @Operation(summary = "Search Regions", description = "Search for regions using filters and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered regions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<RegionDTO>> findRegionByFilter(@RequestBody SearchRequest request, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RegionDTO> regions = regionService.findRegionByFilter(request, pageable);
        if (regions.isEmpty()) {
            logger.info("No regions found for the given filter criteria");
        }
        return ResponseEntity.ok(regions);
    }
}
