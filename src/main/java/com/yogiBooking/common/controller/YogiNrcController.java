package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcCreateDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcUpdateDTO;
import com.yogiBooking.common.service.YogiNrcService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Yogi Nrc", description = "Yogi Nrc APIs")
@APIResource(apiPath = "/yogi-nrcs")
@RequiredArgsConstructor
public class YogiNrcController {

    private final YogiNrcService yogiNrcService;
    private static final Logger logger = LoggerFactory.getLogger(YogiNrcController.class);

    @Operation(summary = "Retrieve all Yogi NRC", description = "Retrieve all Yogi NRC APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieved all Yogi NRC",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = YogiNrcDTO.class)))
            )
    })
    @GetMapping()
    public ResponseEntity<?> getAll() {
        try {
            logger.info("Retrieve all Yogi NRC request");
            var yogiNrc = yogiNrcService.getAll();
            logger.info("Retrieve all Yogi NRC response : {}", JsonUtil.toPrettyJson(yogiNrc));
            return ResponseEntity.ok(yogiNrc);
        } catch (Exception e) {
            logger.error("Error occur in retrieve all Yogi NRC : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Retrieve Yogi NRC by Id", description = "Retrieve Yogi NRC by Id API")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Retrieved Yogi NRC by Id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiNrcDTO.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getYogiNrcById(@PathVariable Long id) {
        try {
            logger.info("Retrieve Yogi NRC request");
            var yogiNrc = yogiNrcService.getById(id);
            logger.info("Retrieve Yogi NRC response : {}", JsonUtil.toPrettyJson(yogiNrc));
            return ResponseEntity.ok(yogiNrc);
        } catch (Exception e) {
            logger.error("Error occur in retrieve Yogi NRC : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Create Yogi NRC", description = "Created Yogi NRC API")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Created Yogi NRC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiNrcDTO.class)
                    )
            )
    })
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody YogiNrcCreateDTO yogiNrcCreateDTO) {
        try {
            logger.info("Create Yogi NRC request");
            var yogiNrc = yogiNrcService.create(yogiNrcCreateDTO);
            logger.info("Created Yogi NRC response : {}", JsonUtil.toPrettyJson(yogiNrc));
            return ResponseEntity.ok(yogiNrc);
        } catch (Exception e) {
            logger.error("Error occur in creating Yogi NRC : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update Yogi NRC", description = "Update Yogi NRC API")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Updated Yogi NRC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = YogiNrcDTO.class)
                    )

            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody YogiNrcUpdateDTO yogiNrcUpdateDTO, @PathVariable Long id) {
        try {
            logger.info("Update Yogi NRC request");
            var updatedYogiNrc = yogiNrcService.update(id, yogiNrcUpdateDTO);
            logger.info("Updated Yogi NRC response : {}", JsonUtil.toPrettyJson(updatedYogiNrc));
            return ResponseEntity.ok(updatedYogiNrc);
        } catch (Exception e) {
            logger.error("Error occur in updating Yogi NRC : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Yogi NRC", description = "Delete Yogi NRC API")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Deleted Yogi NRC",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )

            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            logger.info("Delete Yogi NRC request");
            var deletedYogiNrc = yogiNrcService.delete(id);
            logger.info("Deleted Yogi NRC response : {}", JsonUtil.toPrettyJson(deletedYogiNrc));
            return ResponseEntity.ok(deletedYogiNrc);
        } catch (Exception e) {
            logger.error("Error occur in deleting Yogi NRC : {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Page<YogiNrcDTO>> findYogiGroupByFilter(
            @RequestBody SearchRequest request,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<YogiNrcDTO> yogiNrcDTOS = yogiNrcService.findYogiNrcByFilter(request, pageable);
        return ResponseEntity.ok(yogiNrcDTOS);
    }
}
