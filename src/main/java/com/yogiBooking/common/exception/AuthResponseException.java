package com.yogiBooking.common.exception;

import java.io.IOException;

public class AuthResponseException extends RuntimeException {
    public AuthResponseException(String message) {
        super(message);
    }
    public AuthResponseException(String msg, IOException e) {
        super(msg, e);
    }
}
