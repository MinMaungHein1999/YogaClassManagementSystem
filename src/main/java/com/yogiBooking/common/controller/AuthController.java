package com.yogiBooking.common.controller;

import com.yogiBooking.common.annotation.APIResource;
import com.yogiBooking.common.dto.OtpVerificationRequest;
import com.yogiBooking.common.dto.ForgotPasswordRequest;
import com.yogiBooking.common.dto.auth.AuthenticationRequest;
import com.yogiBooking.common.dto.auth.AuthenticationResponse;
import com.yogiBooking.common.dto.auth.ResetPasswordRequest;
import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.service.AuthenticationService;
import com.yogiBooking.common.service.UserService;
import com.yogiBooking.common.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User Authentication", description = "Endpoints for user authentication and OTP verification.")
@APIResource(apiPath = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns authentication details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserCreateDTO request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user credentials and returns a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                  "email": "admin@yogibooking.com",
                                                  "password": "admin"
                                                }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.authenticate(request, response));
    }

    @Operation(
            summary = "Verify OTP",
            description = "Verifies OTP and activates user account if valid."
    )
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest otpRequest) {
        log.debug("OTP verification request : {}", JsonUtil.toJson(otpRequest));
        userService.activateUser(otpRequest);
        log.debug("User verified successfully: {}", otpRequest.getEmail());
        return ResponseEntity.ok("Account verified successfully.");
    }

    @Operation(
            summary = "Resend Otp / Forgot Password",
            description = "Generates a new OTP and sends it to the user's email."
    )
    @PostMapping({"/forgot-password", "/resend-otp"})
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        log.debug("Forgot Password / Resend otp request: {}", JsonUtil.toJson(request));
        userService.forgotPassword(request);
        log.debug("New OTP sent to email: {}", request.getEmail());
        return ResponseEntity.ok("New OTP sent to your email.");
    }

    @Operation(
            summary = "Reset Password with OTP",
            description = "Verifies the OTP and allows the user to reset the password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reset Password Request",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "user@example.com",
                                              "otp": "123456",
                                              "newPassword": "NewSecurePassword123"
                                            }
                                        """
                            )
                    )
            )
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.debug("Reset Password OTP verification request: {}", resetPasswordRequest.email());
        userService.resetPassword(resetPasswordRequest);
        log.debug("Password reset successfully for: {}", resetPasswordRequest.email());
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the user by invalidating their JWT token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully.");
    }
}
