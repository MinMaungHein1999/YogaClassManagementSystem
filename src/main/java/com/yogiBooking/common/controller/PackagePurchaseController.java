package com.yogiBooking.common.controller;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageCreateDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageDTO;
import com.yogiBooking.common.dto.yogiPackage.YogiPackageUpdateDTO;
import com.yogiBooking.common.service.YogiPackageService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Tag(name = "Package Purchase Management", description = "Package Purchase Management APIs")
@RequestMapping("/package-purchases") // Changed to plural for consistency
@RequiredArgsConstructor
@RestController
public class PackagePurchaseController {

    private final YogiPackageService yogiPackageService;
    private static final Logger logger = LoggerFactory.getLogger(PackagePurchaseController.class);

    @Operation(summary = "Create Yogi Package", description = "Create a new yogi package")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Yogi package created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiPackageDTO.class)))
    })
    @PostMapping
    public ResponseEntity<YogiPackageDTO> createYogiPackage(@RequestBody YogiPackageCreateDTO yogiPackageCreateDTO) {
        YogiPackageDTO yogiPackageDTO = yogiPackageService.createYogiPackage(yogiPackageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(yogiPackageDTO);
    }

    @Operation(summary = "Update Yogi Package", description = "Update an existing yogi package")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Yogi package updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiPackageDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<YogiPackageDTO> updateYogiPackage(@PathVariable Long id, @RequestBody YogiPackageUpdateDTO yogiPackageUpdateDTO) {
        YogiPackageDTO yogiPackageDTO = yogiPackageService.updateYogiPackage(id, yogiPackageUpdateDTO);
        return ResponseEntity.ok(yogiPackageDTO);
    }

    @Operation(summary = "Delete Yogi Package", description = "Delete a Yogi package")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Yogi package deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYogiPackage(@PathVariable Long id) {
        yogiPackageService.deleteYogiPackage(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieve a Yogi Package by ID", description = "Fetch a Yogi Package by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the Yogi Package",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiPackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Yogi Package not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<YogiPackageDTO> findYogiPackageById(@PathVariable Long id) {
        YogiPackageDTO yogiPackageDTO = yogiPackageService.getYogiPackageById(id);
        return ResponseEntity.ok(yogiPackageDTO);
    }

    @Operation(summary = "Retrieve all Yogi Packages", description = "Get all Yogi Packages")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all yogi packages",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = YogiPackageDTO.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<YogiPackageDTO>> getAllYogiPackages() {
        List<YogiPackageDTO> yogiPackages = yogiPackageService.getAllYogiPackages();
        return ResponseEntity.ok(yogiPackages);
    }

    @Operation(summary = "Search Yogi Packages by Filter", description = "Search for Yogi Packages with filtering and pagination")
    @PostMapping("/search")
    public ResponseEntity<Page<YogiPackageDTO>> findYogiPackageByFilter(@RequestBody SearchRequest request, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<YogiPackageDTO> yogiPackages = yogiPackageService.findYogiPackageByFilter(request, pageable);
        if (yogiPackages.isEmpty()) {
            logger.info("No yogi packages found for the given filter criteria");
        }
        return ResponseEntity.ok(yogiPackages);
    }
}