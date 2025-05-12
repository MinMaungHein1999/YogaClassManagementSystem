package com.yogiBooking.common.exception;

import java.util.ArrayList;
import java.util.List;

public class BatchEnrollmentFailedException extends RuntimeException {

    private List<String> errorMessages;

    public BatchEnrollmentFailedException(String message) {
        super(message);
        this.errorMessages = new ArrayList<>();
    }

    public BatchEnrollmentFailedException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }
}
