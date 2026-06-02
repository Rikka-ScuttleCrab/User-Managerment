package com.example.ManagerApp.service;

import com.example.ManagerApp.service.dto.request.SignInRequest;
import com.example.ManagerApp.service.dto.request.SignUpRequest;
import com.example.ManagerApp.service.dto.response.UserLoginResponse;
import com.example.ManagerApp.service.dto.response.MeResponse;

public interface AuthService {

    MeResponse signUp(SignUpRequest request);

    UserLoginResponse signIn(SignInRequest request);

    void resetPassword(String email, String newPassword);
}