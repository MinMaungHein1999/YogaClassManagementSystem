package com.yogiBooking.common.exception;

public class AccountNotConfirmedException extends RuntimeException {
    public AccountNotConfirmedException(String message) {
        super(message);
    }
}
