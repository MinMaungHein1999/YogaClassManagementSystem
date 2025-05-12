package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.service_category.ServiceCategoryDTO;
import com.yogiBooking.common.dto.service_category.ServiceCategoryCreateDTO;
import com.yogiBooking.common.dto.service_category.ServiceCategoryUpdateDTO;
import com.yogiBooking.common.service.ServiceCategoryService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Service Category", description = "Service Category APIs")
@APIResource(apiPath = "/service-categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;
    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryController.class);

    @Operation(summary = "Retrieve all service categories", description = "Get all service category records")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServiceCategoryDTO.class))))
    })
    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all service categories");
        var results = serviceCategoryService.getAll();
        logger.info("Fetched: {}", JsonUtil.toPrettyJson(results));
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Retrieve service category by ID", description = "Get a single service category by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceCategoryDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("Fetching service category with ID {}", id);
        var result = serviceCategoryService.getById(id);
        logger.info("Result: {}", JsonUtil.toPrettyJson(result));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create a service category", description = "Create a new service category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceCategoryDTO.class)))
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceCategoryCreateDTO createDTO) {
        logger.info("Creating new service category: {}", JsonUtil.toPrettyJson(createDTO));
        var result = serviceCategoryService.create(createDTO);
        logger.info("Created: {}", JsonUtil.toPrettyJson(result));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a service category", description = "Update an existing service category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceCategoryDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ServiceCategoryUpdateDTO updateDTO) {
        logger.info("Updating service category ID {} with data: {}", id, JsonUtil.toPrettyJson(updateDTO));
        var updated = serviceCategoryService.update(id, updateDTO);
        logger.info("Updated: {}", JsonUtil.toPrettyJson(updated));
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a service category", description = "Delete a service category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("Deleting service category with ID {}", id);
        var deleted = serviceCategoryService.delete(id);
        logger.info("Deleted: {}", JsonUtil.toPrettyJson(deleted));
        return ResponseEntity.ok(deleted);
    }

    @Operation(summary = "Search service categories by filter", description = "Search service categories using filters and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServiceCategoryDTO.class))))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<ServiceCategoryDTO>> searchByFilter(
            @RequestBody SearchRequest request,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        var result = serviceCategoryService.findServiceCategoryByFilter(request, pageable);
        return ResponseEntity.ok(result);
    }
}

