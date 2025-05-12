package com.yogiBooking.common.exception;

public class VerificationEmailFailedException extends RuntimeException {
    public VerificationEmailFailedException(String message) {
        super(message);
    }
}
