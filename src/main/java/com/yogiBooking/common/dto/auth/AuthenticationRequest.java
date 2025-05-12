package com.yogiBooking.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest (
       @NotNull @NotBlank String email,
       @NotNull @NotBlank String password
){}
