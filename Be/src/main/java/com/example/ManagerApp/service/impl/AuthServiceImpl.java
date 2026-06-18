package com.example.ManagerApp.service.impl;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.ManagerApp.app.rest.error.AccountLockedException;
import com.example.ManagerApp.app.rest.error.BadRequestAlertException;
import com.example.ManagerApp.dao.RoleDAO;
import com.example.ManagerApp.dao.UserDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.security.SecurityService;
import com.example.ManagerApp.service.AuthService;
import com.example.ManagerApp.service.OtpService;
import com.example.ManagerApp.service.dto.request.SignInRequest;
import com.example.ManagerApp.service.dto.request.SignUpRequest;
import com.example.ManagerApp.service.dto.response.MeResponse;
import com.example.ManagerApp.service.dto.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final OtpService otpService;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );
    @Override
    public MeResponse signUp(SignUpRequest request) {
        userDAO.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new BadRequestAlertException("Username already exists");
                });
        if (request.getEmail() == null
            || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BadRequestAlertException(
                    "Invalid email format"
            );
        }
        userDAO.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new BadRequestAlertException("Email already exists");
                });
        if (request.getPassword() == null ||
            request.getPassword().length() < 8) {
        throw new BadRequestAlertException(
        "Password must be at least 8 characters");
        }
        Role role = roleDAO.findByRoleName("Reader")
                .orElseThrow(() ->
                        new RuntimeException("Default role READER not found"));
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setNickname(
                request.getNickname() == null || request.getNickname().isBlank()
                        ? request.getUsername()
                        : request.getNickname()
        );
        // MANY ROLES
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userDAO.save(user);
        return new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getNickname(),
                user.getRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .toList()
        );
    }
    @Override
    public UserLoginResponse signIn(SignInRequest request) {
        User user = userDAO
                .findByUsernameOrEmail(request.getLogin(), request.getLogin())
                .orElseThrow(() ->
                        new BadRequestAlertException("User not found"));
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AccountLockedException("Your account has been locked. Please contact the administrator for more information.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestAlertException("Invalid password");
        }
        String accessToken = securityService.createAccessToken(user);
        String refreshToken = securityService.createRefreshToken(user);
        user.setRefreshToken(refreshToken);
        // ⚠️ cần update refresh token
        userDAO.updateInfo(user);
        System.out.println("ROLES = " + user.getRoles());
        return new UserLoginResponse(accessToken, refreshToken);
    }
    @Override
    public void resetPassword(String email, String newPassword) {
        if (!otpService.canResetPassword(email)) {
            throw new BadRequestAlertException("OTP not verified");
        }
        User user = userDAO.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestAlertException("User not found"));
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AccountLockedException("Your account has been locked. Please contact the administrator for more information.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.updatePassword(user.getId(), user.getPassword());
        otpService.clearResetPasswordPermission(email);
    }
}