package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogi.YogiCreateDTO;
import com.yogiBooking.common.dto.yogi.YogiDTO;
import com.yogiBooking.common.dto.yogi.YogiUpdateDTO;
import com.yogiBooking.common.service.YogiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Yogi Management", description = "Yogi Management APIs")
@APIResource(apiPath = "/yogis")
@RequiredArgsConstructor
@RestController
public class YogiController {

    private final YogiService yogiService;

    private static final Logger logger = LoggerFactory.getLogger(YogiController.class);

    @Operation(summary = "Create Yogi", description = "Create a new yogi")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Yogi created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiDTO.class)))
    })
    @PostMapping
    public ResponseEntity<YogiDTO> createYogi(@RequestBody YogiCreateDTO yogiCreateDTO){
        YogiDTO yogiDTO = yogiService.createYogi(yogiCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(yogiDTO);
    }

    @Operation(summary = "Update Yogi", description = "Update an existing yogi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Yogi updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiDTO.class)))
    })

    @PutMapping("/{id}")
    public ResponseEntity<YogiDTO> updateYogi(@PathVariable Long id, @RequestBody YogiUpdateDTO yogiUpdateDTO){
        YogiDTO yogiDTO = yogiService.updateYogi(id, yogiUpdateDTO);
        return ResponseEntity.ok(yogiDTO);
    }

    @Operation(summary = "Delete Yogi", description = "Delete a Yogi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Yogi deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYogi(@PathVariable Long id){
        yogiService.deleteYogi(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieve a Yogi by ID", description = "Fetch a Yogi by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the Yogi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiDTO.class))),
            @ApiResponse(responseCode = "404", description = "Yogi not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<YogiDTO> findYogiById(@PathVariable Long id){
        YogiDTO yogiDTO= yogiService.findYogiById(id);
        return ResponseEntity.ok(yogiDTO);
    }

    @Operation(summary = "Retrieve all Yogis", description = "Get all Yogis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all yogis",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = YogiDTO.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<YogiDTO>> getAllYogis(){
        List<YogiDTO> yogis = yogiService.getAllYogis();
        return ResponseEntity.ok(yogis);
    }

    @Operation(summary = "Search Yogis by Filter", description = "Search for Yogis with filtering and pagination")
    @PostMapping("/search")
    public ResponseEntity<Page<YogiDTO>> findYogiByFilter(@RequestBody SearchRequest request, @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<YogiDTO> yogis = yogiService.findYogiByFilter(request, pageable);
        if (yogis.isEmpty()) {
            logger.info("No yogis found for the given filter criteria");
        }
        return ResponseEntity.ok(yogis);
    }

}
