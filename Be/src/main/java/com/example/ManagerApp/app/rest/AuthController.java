package com.example.ManagerApp.app.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ManagerApp.service.AuthService;
import com.example.ManagerApp.service.OtpService;
import com.example.ManagerApp.service.dto.request.ResetPasswordRequest;
import com.example.ManagerApp.service.dto.request.SendOtpRequest;
import com.example.ManagerApp.service.dto.request.SignInRequest;
import com.example.ManagerApp.service.dto.request.SignUpRequest;
import com.example.ManagerApp.service.dto.request.VerifyOtpRequest;
import com.example.ManagerApp.service.dto.response.MeResponse;
import com.example.ManagerApp.service.dto.response.MessageResponse;
import com.example.ManagerApp.service.dto.response.UserLoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/sign-up")
    public MeResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/sign-in")
    public UserLoginResponse signIn(@Valid @RequestBody SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(
            @RequestBody SendOtpRequest request) {

        otpService.createAndSendOtp(
                request.getEmail());

        return ResponseEntity.ok(
                new MessageResponse(
                        "OTP sent")
                );
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @RequestBody VerifyOtpRequest request) {

        boolean valid =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp());

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                        "Invalid OTP")
                );
        }

        return ResponseEntity.ok(
                new MessageResponse(
                        "OTP verified")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getEmail(),
                request.getNewPassword());

        return ResponseEntity.ok(
                new MessageResponse(
                "Password reset success"));
    }
}