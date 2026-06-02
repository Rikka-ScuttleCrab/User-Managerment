package com.example.ManagerApp.app.rest.error;

public class NotFoundAlertException extends RuntimeException {
    public NotFoundAlertException(String message) {
        super(message);
    }
}
