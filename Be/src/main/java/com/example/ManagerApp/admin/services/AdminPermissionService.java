package com.example.ManagerApp.admin.services;


import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

import com.example.ManagerApp.admin.services.dto.request.TogglePermissionRequest;
import com.example.ManagerApp.admin.services.dto.request.UpdateRolePermissionsRequest;
import com.example.ManagerApp.admin.services.dto.response.PermissionItemResponse;
import com.example.ManagerApp.admin.services.dto.response.RolePermissionsResponse;
import com.example.ManagerApp.admin.services.dto.response.TogglePermissionResponse;
import com.example.ManagerApp.admin.services.dto.response.UpdateRolePermissionsResponse;

public interface AdminPermissionService {

    boolean checkPermission(Jwt jwt, String permissionName, String actionName);

    TogglePermissionResponse togglePermission(TogglePermissionRequest request);

    List<RolePermissionsResponse> getAllRolePermissions();

    List<PermissionItemResponse> getCurrentPermissions(Jwt jwt);

    UpdateRolePermissionsResponse updateRolePermissions(UpdateRolePermissionsRequest request);
}