package com.yogiBooking.common.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.yogiBooking.common.utils.constants.TokenNameConstants.ACCESS_TOKEN;
import static com.yogiBooking.common.utils.constants.TokenNameConstants.REFRESH_TOKEN;

public record AuthenticationResponse (
  @JsonProperty(ACCESS_TOKEN) String accessToken,
  @JsonProperty(REFRESH_TOKEN) String refreshToken
){}
