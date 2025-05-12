package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.level.LevelCreateDTO;
import com.yogiBooking.common.dto.level.LevelDTO;
import com.yogiBooking.common.dto.level.LevelUpdateDTO;
import com.yogiBooking.common.service.LevelService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Level Class Management", description = "Level Class Management APIs")
@APIResource(apiPath = "/levels")
@RequiredArgsConstructor
public class LevelController {
    private final LevelService levelService;
    private static final Logger logger = LoggerFactory.getLogger(LevelController.class);

    @Operation(summary = "Create Level Class", description = "Create a new level")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Level class created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LevelDTO.class)))
    })
    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@RequestBody LevelCreateDTO levelCreateDTO){
        try{
            logger.info("Create Level ");
            LevelDTO levelDTO = this.levelService.createLevel(levelCreateDTO);
            logger.info("Created Level : {}", JsonUtil.toPrettyJson(levelDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(levelDTO);
        }catch(Exception e){
            logger.error("Error occur in creating level : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating level", e);
        }
    }

    @Operation(summary = "Update Level Class", description = "Update an existing level class API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Level class updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LevelDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable Long id, @RequestBody LevelUpdateDTO levelUpdateDTO){
        try{
            logger.info("Updating Level with ID: {}", id);
            LevelDTO levelDTO = levelService.updateLevel(id,levelUpdateDTO);
            logger.info("Updated level : {}",JsonUtil.toPrettyJson(levelDTO));
            return ResponseEntity.ok(levelDTO);
        }catch (Exception e){
            logger.error("Error updating Level : {}",e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Level", description = "Delete Level API")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Level deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id){
        try {
            logger.info("Request to delete level with ID: {}", id);
            levelService.deleteLevel(id);
            logger.info("Level with ID {} deleted successfully.", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error in deleting level with ID: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Retrieve a Level by ID", description = "Fetch a Level by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the level",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LevelDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LevelDTO> findLevelById(@PathVariable Long id) {
        try {
            logger.info("Find Level by ID request: {}", id);
            var level = levelService.findLevelById(id);
            logger.info("Retrieved Level: {}", JsonUtil.toPrettyJson(level));
            return ResponseEntity.ok(level);
        } catch (Exception e) {
            logger.error("Error in finding level by ID: {}", e.getMessage());
            throw e;
        }
    }


    @Operation(summary = "Retrieve all Levels", description = "Retrieve all Levels API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all levels",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LevelDTO.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels(){
        logger.info("Retrieve all Levels request");
        List<LevelDTO> levels = levelService.getAllLevels();
        logger.info("Retrieve all Levels response: {}", JsonUtil.toPrettyJson(levels));
        return ResponseEntity.ok(levels);
    }


    @Operation(summary = "Search Levels by Filter", description = "Search for levels with filtering and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered levels",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<LevelDTO>> findLevelByFilter(@RequestBody SearchRequest request, @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<LevelDTO> levels = levelService.findLevelByFilter(request,pageable);
        if (levels.isEmpty()) {
            logger.info("No levels found for the given filter criteria");
        }
        return  ResponseEntity.ok(levels);
    }
}
