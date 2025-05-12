package com.yogiBooking.common.config;

import com.yogiBooking.common.auditing.ApplicationAuditAware;
import com.yogiBooking.common.dto.MyUserDetails;
import com.yogiBooking.common.repository.TokenRepository;
import com.yogiBooking.common.repository.UserRepository;
import com.yogiBooking.common.service.CookieService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Arrays;

import static com.yogiBooking.common.utils.constants.TokenNameConstants.ACCESS_TOKEN;
import static com.yogiBooking.common.utils.constants.TokenNameConstants.REFRESH_TOKEN;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    @Value("${environment}")
    private String environment;

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> new MyUserDetails(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(email))));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final int PASSWORD_STRENGTH = 12;
        return new BCryptPasswordEncoder(PASSWORD_STRENGTH);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public LogoutHandler logoutHandler(CookieService cookieService) {
        return (request, response, authentication) -> {
            Cookie[] cookies = request.getCookies();
            final String accessToken = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(ACCESS_TOKEN))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            final String refreshToken = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(REFRESH_TOKEN))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (refreshToken == null || accessToken == null) {
                return;
            }

            tokenRepository.findByToken(accessToken).ifPresent(token -> {
                token.setExpired(true);
                token.setRevoked(true);
                tokenRepository.save(token);
                SecurityContextHolder.clearContext();
            });

            // Clear cookies using CookieService
            cookieService.removeCookie(response, ACCESS_TOKEN);
            cookieService.removeCookie(response, REFRESH_TOKEN);
        };
    }
}
