package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.city.CityCreateDTO;
import com.yogiBooking.common.dto.city.CityDTO;
import com.yogiBooking.common.dto.city.CityUpdateDTO;
import com.yogiBooking.common.service.CityService;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Tag(name = "City Management", description = "City Management APIs")
@APIResource(apiPath = "/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    @Operation(summary = "Create City", description = "Create a new city")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "City created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CityDTO.class)))
    })
    @PostMapping
    public ResponseEntity<CityDTO> createCity(@RequestBody CityCreateDTO cityCreateDTO) {
        try {
            logger.info("Creating City");
            CityDTO cityDTO = cityService.createCity(cityCreateDTO);
            logger.info("Created City: {}", JsonUtil.toPrettyJson(cityDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(cityDTO);
        } catch (Exception e) {
            logger.error("Error creating city: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating city", e);
        }
    }

    @Operation(summary = "Update City", description = "Update an existing city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City class updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CityDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CityDTO> updateCity(@PathVariable Long id, @RequestBody CityUpdateDTO cityUpdateDTO){
        try {
            logger.info("Updating City with ID: {}", id);
            CityDTO cityDTO = cityService.updateCity(id, cityUpdateDTO);
            logger.info("Updated City: {}", JsonUtil.toPrettyJson(cityDTO));
            return ResponseEntity.ok(cityDTO);
        } catch (Exception e) {
            logger.error("Error updating city: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete City", description = "Delete a city by ID")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "City deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id){
        logger.info("Request to delete city with ID: {}", id);
        if (cityService.findById(id).isEmpty()) {
            logger.warn("City with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City with ID " + id + " not found");
        }
        cityService.deleteCity(id);
        logger.info("City with ID {} deleted successfully.", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieve a City by ID", description = "Fetch a city by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the city",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CityDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "City not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> findById(@PathVariable Long id){
        logger.info("Find City by ID request: {}", id);
        Optional<CityDTO> cityOptional = cityService.findById(id);
        if (cityOptional.isEmpty()) {
            logger.warn("City with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City with ID " + id + " not found");
        }
        CityDTO city = cityOptional.get();
        logger.info("Retrieved City: {}", JsonUtil.toPrettyJson(city));
        return ResponseEntity.ok(city);
    }

    @Operation(summary = "Retrieve all Cities", description = "Retrieve a list of all cities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all cities",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CityDTO.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<CityDTO>> getAllCities(){
        logger.info("Retrieve all Cities request");
        List<CityDTO> cities = cityService.getAllCities();
        logger.info("Retrieve all Cities response: {}", JsonUtil.toPrettyJson(cities));
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Search Cities by Filter", description = "Search for cities with filtering and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered cities",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<CityDTO>> findCityByFilter(@RequestBody SearchRequest request,@RequestParam int page,@RequestParam int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<CityDTO> cities = cityService.findCityByFilter(request, pageable);
        if (cities.isEmpty()) {
            logger.info("No cities found for the given filter criteria");
        }
        return ResponseEntity.ok(cities);
    }
}
