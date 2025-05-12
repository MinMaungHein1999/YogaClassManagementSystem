package com.yogiBooking.common.service;

import com.yogiBooking.common.entity.User;
import com.yogiBooking.common.exception.OtpExpiredException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.exception.sentOtpFailedException;
import com.yogiBooking.common.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository; // Inject UserRepository
    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes
    private final EmailService emailService;

    private String generateOtp(){
        return String.format("%06d", new Random().nextInt(999999));
    }

    public boolean isOtpInvalid(String email, String otp) {
        User user = userRepository.findByEmail(email).
                orElseThrow(()-> new ResourceNotFoundException(String.format("User Not Found for email: %s", email)));
        Long otpGeneratedAt = user.getOtpGeneratedAt();

        if (System.currentTimeMillis() > otpGeneratedAt + OTP_EXPIRATION_TIME) {
            throw new OtpExpiredException(
                    String.format("OTP has expired. It was generated at %d and is valid for %d milliseconds.",
                            otpGeneratedAt, OTP_EXPIRATION_TIME));
        }

        boolean valid = Objects.equals(otp, user.getOtp());
        if (valid) {
            user.setOtp(null);
            user.setOtpGeneratedAt(0L);
            userRepository.save(user);
        }
        return !valid;
    }

    public void sendOtp(User user) {
        var otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(System.currentTimeMillis());
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (MessagingException e) {
            throw new sentOtpFailedException("Failed to send Otp : ", e);
        }
    }
}

