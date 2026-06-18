package com.example.ManagerApp.app.rest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ManagerApp.service.OtpService;
import com.example.ManagerApp.service.dto.request.SendOtpRequest;
import com.example.ManagerApp.service.dto.request.VerifyOtpRequest;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/v1/api/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(
            @RequestBody SendOtpRequest request) {
        otpService.createAndSendOtp(
                request.getEmail());
        return ResponseEntity.ok(
                "OTP sent successfully");
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody VerifyOtpRequest request) {
        boolean valid =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp());
        if (valid) {
            return ResponseEntity.ok(
                    "OTP verified");
        }
        return ResponseEntity.badRequest()
                .body("Invalid or expired OTP");
    }
}