package com.example.ManagerApp.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ManagerApp.app.rest.error.AccountLockedException;
import com.example.ManagerApp.app.rest.error.BadRequestAlertException;
import com.example.ManagerApp.app.rest.error.NotFoundAlertException;
import com.example.ManagerApp.dao.UserDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.service.UserService;
import com.example.ManagerApp.service.dto.request.ChangePasswordRequest;
import com.example.ManagerApp.service.dto.request.UpdateMeRequest;
import com.example.ManagerApp.service.dto.response.MeResponse;
import com.example.ManagerApp.service.dto.response.MessageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MeResponse updateMe(Long currentUserId, UpdateMeRequest request) {

        User user = userDAO.findById(currentUserId)
                .orElseThrow(() ->
                        new NotFoundAlertException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AccountLockedException("Your account has been locked. Please contact the administrator for more information.");
        }

        // EMAIL
        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())) {

            userDAO.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        throw new BadRequestAlertException(
                                "Email already exists");
                    });

            user.setEmail(request.getEmail());
        }

        // GENDER
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        // NICKNAME
        if (request.getNickname() != null
                && !request.getNickname().isBlank()) {

            user.setNickname(request.getNickname());
        }

        userDAO.updateInfo(user);

        return new MeResponse(
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
    public MeResponse getMe(Long currentUserId) {

        User user = userDAO.findById(currentUserId)
                .orElseThrow(() ->
                        new NotFoundAlertException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AccountLockedException("Your account has been locked. Please contact the administrator for more information.");
        }

        return new MeResponse(
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
    public MessageResponse changePassword(Long currentUserId,
                                         ChangePasswordRequest request) {

        User user = userDAO.findById(currentUserId)
                .orElseThrow(() -> new NotFoundAlertException("User not found"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AccountLockedException("Your account has been locked. Please contact the administrator for more information.");
        }

        // CHECK OLD PASSWORD
        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new BadRequestAlertException("Old password is incorrect");
        }

        // CHECK CONFIRM PASSWORD
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new BadRequestAlertException("Confirm password does not match");
        }

        // CHECK SAME PASSWORD
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new BadRequestAlertException("New password must be different");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userDAO.updatePassword(user.getId(), user.getPassword());

        return new MessageResponse("Password changed successfully");
    }
}