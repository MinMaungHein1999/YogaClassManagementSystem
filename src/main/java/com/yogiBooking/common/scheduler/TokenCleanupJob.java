package com.yogiBooking.common.scheduler;

import com.yogiBooking.common.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupJob {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void cleanExpiredAndRevokedTokens() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1);
        int deletedCount = tokenRepository.deleteOldInvalidTokens(cutoffTime);
        log.info("Deleted {} expired/revoked tokens created before {}", deletedCount, cutoffTime);
    }
}
