package com.yogiBooking.common.exception;
/**
 * Custom exception to handle Bad Request scenarios.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
