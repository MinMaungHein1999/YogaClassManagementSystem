package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.auth.ChangePasswordRequest;
import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.dto.UserRequest;
import com.yogiBooking.common.service.UserService;
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

@Tag(name = "User Management", description = "User Management APIs")
@APIResource(apiPath = "/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Operation(summary = "Retrieve All Users", description = "Retrieve all user APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieve all users",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
    })
    @GetMapping()
    public ResponseEntity<?> getAllUser() {
        try {
            logger.info("Retrieve all users request");
            var users = userService.getAllUser();
            logger.info("Retrieve all user response : {}", JsonUtil.toPrettyJson(users));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occur in retrieve all users : {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Retrieve User By Id", description = "Retrieve user by id APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieve user by id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            logger.info("Retrieve  user by id request");
            var user = userService.getById(id);
            logger.info("Retrieve  user by id response : {}", JsonUtil.toPrettyJson(user));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error occur in retrieve user by id: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Create User", description = "Create user APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create user",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Long.class))))
    })
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            logger.info("User create request : {}", JsonUtil.toPrettyJson(userCreateDTO));
            var userId = userService.createUser(userCreateDTO);
            logger.info("User create response : {}", JsonUtil.toPrettyJson(userId));
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            logger.error("Error occur in create user : {}", e.getMessage());
            throw e;
        }

    }

    @Operation(summary = "Update User", description = "Update user APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        try {
            logger.info("User update with id : {} request : {}", id, JsonUtil.toPrettyJson(userRequest));
            var uptUser = userService.updateUser(id, userRequest);
            logger.info("User update response : {}", JsonUtil.toPrettyJson(uptUser));
            return ResponseEntity.ok(uptUser);
        } catch (Exception e) {
            logger.error("Error occur in update user : {}", e.getMessage());
            throw e;
        }

    }

    @Operation(summary = "Delete User", description = "Delete user APIs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delete user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            logger.info("User delete with id : {} ", id);
            var deleteStatus = userService.deleteUser(id);
            logger.info("User delete response : {}", JsonUtil.toPrettyJson(deleteStatus));
            return ResponseEntity.ok(deleteStatus);
        } catch (Exception e) {
            logger.error("Error occur in delete user : {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Page<UserDTO>> findUserByFilter(
            @RequestBody SearchRequest request,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userDTOS = userService.findUserByFilter(request, pageable);
        return ResponseEntity.ok(userDTOS);
    }

    @Operation(
            summary = "Change Old Password",
            description = "Verifies the user with Old Password to reset the password."
    )
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        logger.debug("Change Password OTP verification request: {}", changePasswordRequest.email());
        userService.changePassword(changePasswordRequest);
        logger.debug("Password change successfully for: {}", changePasswordRequest.email());
        return ResponseEntity.ok("Password has been changed successfully.");
    }
}
