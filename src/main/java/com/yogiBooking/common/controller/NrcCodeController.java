package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeCreateDto;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeDTO;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeUpdateDto;
import com.yogiBooking.common.service.NrcCodeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Nrc Code Management", description = "Nrc Code Management APIs")
@RequiredArgsConstructor
@APIResource(apiPath = "/nrc-codes")
public class NrcCodeController {

    private static final Logger logger = LoggerFactory.getLogger(NrcCodeController.class);
    private final NrcCodeService nrcCodeService;

    @Operation(summary = "Retrieve All Nrc Codes", description = "Retrieve all nrc codes APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieve all nrc codes",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = NrcCodeDTO.class))
                    )
            )
    })
    @GetMapping()
    public ResponseEntity<?> getAllNrcCodes() {
        try {
            logger.info("Retrieve All Nrc Codes");
            var nrcCodes = nrcCodeService.getAllNrcCodes();
            logger.info("Retrieve All Nrc Codes response: {}", JsonUtil.toPrettyJson(nrcCodes));
            return ResponseEntity.ok(nrcCodes);
        } catch (Exception e) {
            logger.error("Error Occur in retrieve all nrc codes : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Retrieve Nrc Code By Id", description = "Retrieve nrc code by id APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieve nrc code by id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NrcCodeDTO.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getNrcCodeById(@PathVariable Long id) {
        try {
            logger.info("Retrieve nrc code by id request");
            var nrcCode = nrcCodeService.getNrcCodeById(id);
            logger.info("Retrieve nrc code by id response : {}", JsonUtil.toPrettyJson(nrcCode));
            return ResponseEntity.ok(nrcCode);
        } catch (Exception e) {
            logger.error("Error Occur in retrieve nrc code by id : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Create Nrc code", description = "Create nrc code APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Nrc code created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NrcCodeDTO.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createNewNrcCode(@RequestBody NrcCodeCreateDto nrcCodeCreateDto) {
        try {
            logger.info("Create nrc code request : {}", JsonUtil.toPrettyJson(nrcCodeCreateDto));
            var nrcCodeDTO = nrcCodeService.createNrcCode(nrcCodeCreateDto);
            logger.info("Create nrc code response : {}", JsonUtil.toPrettyJson(nrcCodeDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(nrcCodeDTO);
        } catch (Exception e) {
            logger.error("Error Occur in create nrc code : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update Nrc Code", description = "Update nrc code APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nrc code updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NrcCodeDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNrcCode(@PathVariable Long id,
                                    @RequestBody NrcCodeUpdateDto nrcCodeUpdateDTO) {
        try {
            logger.info("Nrc code update request with id: {} request : {}", id, JsonUtil.toPrettyJson(nrcCodeUpdateDTO));
            var nrcCode = nrcCodeService.updateNrcCode(id, nrcCodeUpdateDTO);
            logger.info("Nrc code update response : {}", JsonUtil.toPrettyJson(nrcCode));
            return ResponseEntity.ok(nrcCode);
        } catch (Exception e) {
            logger.error("Error Occur in update nrc code : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Nrc Code", description = "Delete nrc code APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nrc code deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNrcCode(@PathVariable Long id) {
        try {
            logger.info("Delete nrc code request with id: {}", id);
            var deleteStatus = nrcCodeService.deleteNrcCode(id);
            logger.info("Delete nrc code response : {}", JsonUtil.toPrettyJson(deleteStatus));
            return ResponseEntity.ok(deleteStatus);
        } catch (Exception e) {
            logger.error("Error Occur in delete nrc code : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Find Nrc Codes By Filter", description = "Find nrc codes by filter APIs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Find nrc codes by filter",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = NrcCodeDTO.class))
                    )
            )
    })
    @PostMapping("/search")
    public ResponseEntity<?> findNrcCodesByFilter(
            @RequestBody SearchRequest request,
            @RequestParam int page,
            @RequestParam int size) {
        try {
            logger.info("Find nrc codes by filter request : {}", request);
            Pageable pageable = PageRequest.of(page, size);
            var nrcCodes = nrcCodeService.findNrcCodesByFilter(request, pageable);
            return ResponseEntity.ok(nrcCodes);
        } catch (Exception e) {
            logger.error("Error Occur in find nrc codes by filter : {}", e.getMessage());
            throw e;
        }
    }

}
