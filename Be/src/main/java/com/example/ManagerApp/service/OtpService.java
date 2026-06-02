package com.example.ManagerApp.service;

import org.springframework.stereotype.Service;


@Service
public interface OtpService {

    
    public void createAndSendOtp(String email);

    // verify OTP
    public boolean verifyOtp( String email, String userOtp);

    boolean canResetPassword(String email);

    void clearResetPasswordPermission(String email);
}