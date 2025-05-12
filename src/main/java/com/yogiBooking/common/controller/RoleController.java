package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.RoleDTO;
import com.yogiBooking.common.service.RoleService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role Management", description = "Role Management APIs")
@APIResource(apiPath = "/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Operation(summary = "Get all roles", description = "Get all roles APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get all roles",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class))))
    })
    @GetMapping
    public ResponseEntity<?> getRoles() {
        try {
            logger.info("Get roles request");
            var roles = roleService.getRoles();
            logger.info("Get roles response : {}", JsonUtil.toPrettyJson(roles));
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error occur in getting roles : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Create Role", description = "Create role APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created role",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class))))
    })
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
        try {
            logger.info("Create role request");
            var role = roleService.createRole(roleDTO);
            logger.info("Created role response : {}", JsonUtil.toPrettyJson(role));
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("Error occur in creating role : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update Role", description = "Update role APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated role",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class))))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        try {
            logger.info("Update role request");
            var role = roleService.updateRole(id, roleDTO);
            logger.info("Updated role response : {}", JsonUtil.toPrettyJson(role));
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("Error occur in updating role : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete Role", description = "Delete role APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted role",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Boolean.class))))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            logger.info("Delete role request");
            var result = roleService.deleteRole(id);
            logger.info("Deleted role response : {}", JsonUtil.toPrettyJson(result));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error occur in deleting role : {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/search")
    public Page<RoleDTO> searchRoles(@RequestParam String name,
                                     @RequestParam String description,
                                     @RequestParam int page,
                                     @RequestParam int size) {
        return roleService.searchRoles(name, description, page, size);
    }
}

