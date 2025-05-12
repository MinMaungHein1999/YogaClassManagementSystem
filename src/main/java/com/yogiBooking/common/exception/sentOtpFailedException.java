package com.yogiBooking.common.exception;

import jakarta.mail.MessagingException;

public class sentOtpFailedException extends RuntimeException {
    public sentOtpFailedException(String message, MessagingException e) {
        super(message, e);
    }
    public sentOtpFailedException(String message) {
        super(message);
    }
}
