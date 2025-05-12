package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.auth.AuthenticationRequest;
import com.yogiBooking.common.dto.auth.AuthenticationResponse;
import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.entity.Token;
import com.yogiBooking.common.entity.TokenType;
import com.yogiBooking.common.exception.AccountNotConfirmedException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.repository.TokenRepository;
import com.yogiBooking.common.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.yogiBooking.common.utils.constants.TokenNameConstants.ACCESS_TOKEN;
import static com.yogiBooking.common.utils.constants.TokenNameConstants.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CookieService cookieService;

    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    public AuthenticationResponse register(UserCreateDTO request) {
        var savedUser = userService.createUser(request);
        var jwtToken = jwtService.generateToken(savedUser.getEmail());
        var refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());
        saveUserToken(savedUser.getId(), jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User with %s not found".formatted(request.email())));
        if (Objects.isNull(user.getConfirmedAt())) {
            throw new AccountNotConfirmedException("Your account is not confirmed. Please check your email.");
        }
        var jwtToken = jwtService.generateToken(user.getEmail());
        var refreshToken = jwtService.generateRefreshToken(user.getEmail());
        revokeAllUserTokens(user.getId());
        saveUserToken(user.getId(), jwtToken);
        Cookie accessTokenCookie = cookieService.createCookie(ACCESS_TOKEN, jwtToken, jwtExpiration);
        Cookie refreshTokenCookie = cookieService.createCookie(REFRESH_TOKEN, refreshToken, refreshExpiration);
        cookieService.addCookie(response, accessTokenCookie);
        cookieService.addCookie(response, refreshTokenCookie);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(Long userId, String jwtToken) {
        var token = Token.builder()
                .userId(userId)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Long userId) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(userId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public Cookie generateNewAccessToken(String refreshToken) {
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            log.warn("Failed to extract username from refresh token");
            return null;
        }
        var user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userEmail));
        if (jwtService.isTokenValid(refreshToken, user.getEmail())) {
            var newAccessToken = jwtService.generateToken(user.getEmail());
            revokeAllUserTokens(user.getId());
            saveUserToken(user.getId(), newAccessToken);
            return cookieService.createCookie(ACCESS_TOKEN, newAccessToken, jwtExpiration);
        }
        return null;
    }
}