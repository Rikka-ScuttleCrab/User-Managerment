package com.example.ManagerApp.app.rest.error;
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}