package com.example.ManagerApp.service.impl;
import com.example.ManagerApp.service.OtpService;
import com.example.ManagerApp.service.dto.OtpData;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class OtpServiceImpl implements OtpService {
    private final Map<String, OtpData> otpStorage =
            new ConcurrentHashMap<>();
    private final JavaMailSender mailSender;
    private final Map<String, Boolean> verifiedResetPassword =
        new ConcurrentHashMap<>();
    public OtpServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public String generateOtp() {
        Random random = new Random();
        return String.valueOf(
                100000 + random.nextInt(900000));
    }
    public void sendEmail(String receiverEmail, String otp) {
        SimpleMailMessage message =
                new SimpleMailMessage();
        message.setFrom("ticketscinemasupport@gmail.com");
        message.setTo(receiverEmail);
        message.setSubject("Your OTP Code");
        message.setText(
                "Your OTP is "
                        + otp
                        + ". It expires in 5 minutes.");
        mailSender.send(message);
    }
    @Override
    public void createAndSendOtp(String email) {
        String otp = generateOtp();
        otpStorage.put(
                email,
                new OtpData(
                        otp,
                        System.currentTimeMillis()));
        sendEmail(email, otp);
    }
    @Override
    public boolean verifyOtp(
        String email,
        String userOtp) {
        OtpData saved = otpStorage.get(email);
        if (saved == null) {
            return false;
        }
        long currentTime =
                System.currentTimeMillis();
        if (currentTime - saved.getCreatedTime()
                > 5 * 60 * 1000) {
            otpStorage.remove(email);
            return false;
        }
        if (saved.getOtp().equals(userOtp)) {
            otpStorage.remove(email);
            verifiedResetPassword.put(email, true);
            return true;
        }
        return false;
    }
    @Override
    public boolean canResetPassword(String email) {
        return verifiedResetPassword.getOrDefault(email, false);
    }
    @Override
    public void clearResetPasswordPermission(String email) {
        verifiedResetPassword.remove(email);
    }
}
