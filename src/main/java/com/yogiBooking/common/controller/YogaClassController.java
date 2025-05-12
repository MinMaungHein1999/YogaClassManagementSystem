package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchFilter;
import com.yogiBooking.common.dto.yoga_class.YogaClassCreateDTO;
import com.yogiBooking.common.dto.yoga_class.YogaClassDTO;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yoga_class.YogaClassUpdateDTO;
import com.yogiBooking.common.dto.yogi.YogiDTO;
import com.yogiBooking.common.service.yogaClass.YogaClassService;
import com.yogiBooking.common.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FieldResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Yoga Class Management", description = "Yoga Class Management APIs")
@APIResource(apiPath = "/yoga-classes")
@RequiredArgsConstructor
public class YogaClassController {

    private final YogaClassService yogaClassService;
    private static final Logger logger = LoggerFactory.getLogger(YogaClassController.class);

    @Operation(summary = "Saves a yoga class", description = "This method creates a new yoga class in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the newly created yoga class in JSON format.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = YogaClassDTO.class)))
    })
    @PostMapping
    public ResponseEntity<YogaClassDTO> handleSaveYogaClassRequest(@RequestBody YogaClassCreateDTO yogaClassCreateDTO){
        try {
            logger.info("Request to save a new yoga class {}", JsonUtil.toPrettyJson(yogaClassCreateDTO));
            var yogaClass = yogaClassService.saveNewYogaClass(yogaClassCreateDTO);
            logger.info("Saved yoga class {}", JsonUtil.toPrettyJson(yogaClass));
            return ResponseEntity.ok(yogaClass);
        } catch (Exception e) {
            logger.error("Error saving yoga class {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get waiting yogi list by class ID", description = "Retrieves a list of yogis waiting for a specific yoga class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the list of waiting yogis.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = YogiDTO.class, type = "array"))),
            @ApiResponse(responseCode = "404", description = "Class not found.")
    })
    @GetMapping("/{classId}/waiting-list")
    public ResponseEntity<List<YogiDTO>> getWaitingYogiListByClassId(@PathVariable Long classId) {
            logger.info("Request to get waiting list for class ID: {}", classId);
            List<YogiDTO> waitingList = yogaClassService.getWaitingYogiListByClassId(classId);
            if (waitingList == null) {
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} yogis in waiting list for class ID: {}", waitingList.size(), classId);
            return ResponseEntity.ok(waitingList);
    }


    @Operation(summary = "Finds yoga classes by specific criteria", description = "This method searches the database for yoga classes that match the provided search criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of matched yoga classes in JSON format.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = YogaClassDTO.class))))
    })
    @PostMapping("/search-by-country-id")
    public ResponseEntity<Page<YogaClassDTO>> handleSearchYogaClass(
            @RequestParam(required = false) Long countryId,
            @RequestParam(defaultValue = "0") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        SearchRequest searchRequest = new SearchRequest();
        List<SearchFilter> searchFilters = new ArrayList<>();

        if (countryId != null) {
            SearchFilter searchFilter = new SearchFilter();
            searchFilter.setType("number");
            searchFilter.setTerm("country.id");
            searchFilter.setMatchType("exact");
            searchFilter.setValue(countryId.toString());
            searchFilters.add(searchFilter);
            logger.info("Searching yoga classes by country ID: {}", countryId);
        } else {
            logger.info("Searching yoga classes with no specific country ID filter.");
        }

        searchRequest.setFilters(searchFilters);
        logger.info("Request to find yoga classes by filters {}", JsonUtil.toPrettyJson(searchFilters));
        var resultPages = yogaClassService.findYogaClassesByFilters(searchRequest, PageRequest.of(currentPage, pageSize));
        logger.info("Found {} yoga classes", resultPages.getTotalElements());
        return ResponseEntity.ok(resultPages);
    }


    @Operation(summary = "Finds yoga classes", description = "This method searches the database for yoga classes that match the search criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of matched yoga classes in JSON format.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = YogaClassDTO.class))))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<YogaClassDTO>> handleSearchYogaClassRequest(@RequestBody SearchRequest searchRequest,
                                                                           @RequestParam int currentPage,
                                                                           @RequestParam int pageSize){
        logger.info("Request to find yoga classes by filters {}", JsonUtil.toPrettyJson(searchRequest));
        var resultPages = yogaClassService.findYogaClassesByFilters(searchRequest, PageRequest.of(currentPage, pageSize));
        logger.info("Found {} yoga classes", resultPages.getTotalElements());
        return ResponseEntity.ok(resultPages);
    }

    @Operation(summary = "Gets all yoga classes", description = "This method retrieves every yoga class from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of yoga classes in JSON format.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = YogaClassDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<YogaClassDTO>> handleGetAllYogaClassesRequest(){
        try {
            logger.info("Request to get all yoga classes");
            var yogaClasses = yogaClassService.getAllYogaClasses();
            logger.info("Found yoga classes {}", yogaClasses);
            return ResponseEntity.ok(yogaClasses);
        } catch (Exception e) {
            logger.error("Error getting all yoga classes {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Finds a yoga class by ID", description = "This method retrieves a yoga class by ID from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a yoga class in JSON format.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = YogaClassDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<YogaClassDTO> handleGetYogaClassByIdRequest(@PathVariable Long id){
        try {
            logger.info("Request to find yoga class by ID {}", id);
            var yogaClass = yogaClassService.findYogaClassById(id);
            logger.info("Found yoga class {}", JsonUtil.toPrettyJson(yogaClass));
            return ResponseEntity.ok(yogaClass);
        } catch (Exception e) {
            logger.error("Error finding yoga class by ID {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Updates a yoga class", description = "This method updates the information of an existing yoga class in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the updated yoga class in JSON format.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = YogaClassDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<YogaClassDTO> handleUpdateYogaClassRequest(@PathVariable Long id,
                                                                     @RequestBody YogaClassUpdateDTO yogaClassUpdateDTO) {
        try {
            logger.info("Update yoga class with ID {}", id);
            var updatedYogaClass = yogaClassService.updateExistingYogaClass(id, yogaClassUpdateDTO);
            logger.info("Updated yoga class {}", JsonUtil.toPrettyJson(updatedYogaClass));
            return ResponseEntity.ok(updatedYogaClass);
        } catch (Exception e) {
            logger.error("Error updating yoga class {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Deletes a yoga class", description = "This method deletes an existing yoga class by ID from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a string to indicate that the delete process is successful.",
                    content = @Content(mediaType = "text/html"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> handleDeleteYogaClassRequest(@PathVariable Long id){
        try {
            logger.info("Request to delete yoga class with ID {}", id);
            yogaClassService.deleteYogaClass(id);
            logger.info("Deleted yoga class with ID {}", id);
            return ResponseEntity.ok("Delete successful");
        } catch (Exception e) {
            logger.error("Error deleting yoga class with ID {}", e.getMessage());
            throw e;
        }
    }
}

