package com.yogiBooking.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String email,
        @NotBlank @Size(min = 6, max = 6) String otp,
        @NotBlank String newPassword
)
{}
