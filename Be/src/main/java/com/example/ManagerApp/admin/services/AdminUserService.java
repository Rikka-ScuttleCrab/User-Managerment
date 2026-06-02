package com.example.ManagerApp.admin.services;

import java.util.List;

import com.example.ManagerApp.admin.services.dto.request.AdminUpdateUserInfoRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminUserPageResponse;
import com.example.ManagerApp.admin.services.dto.response.AdminUserResponse;
import com.example.ManagerApp.admin.services.dto.response.ToggleActiveResponse;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.service.dto.response.MessageResponse;

public interface AdminUserService {

     AdminUserPageResponse getAllUsers(Long currentUserId,  int page);

     User updateUserInfo(Long id, AdminUpdateUserInfoRequest request);

     ToggleActiveResponse toggleActive(Long currentUserId, Long id);

     void assignRolesToUser(Long userId, List<Long> roleIds);

     MessageResponse resetUserPassword(Long currentUserId, Long targetUserId);

     List<AdminUserResponse> searchUsers(Long currentUserId, String keyword, String role, String gender, String status);
}

