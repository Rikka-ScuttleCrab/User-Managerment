package com.example.ManagerApp.service;

import com.example.ManagerApp.service.dto.request.ChangePasswordRequest;
import com.example.ManagerApp.service.dto.request.UpdateMeRequest;
import com.example.ManagerApp.service.dto.response.MeResponse;
import com.example.ManagerApp.service.dto.response.MessageResponse;


public interface UserService {

    MeResponse getMe(Long currentUserId);

    MeResponse updateMe(Long currentUserId, UpdateMeRequest request);

    MessageResponse changePassword(Long currentUserId,
                               ChangePasswordRequest request);
}