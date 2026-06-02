package com.example.ManagerApp.admin.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ManagerApp.admin.services.AdminPermissionService;
import com.example.ManagerApp.admin.services.dto.request.CheckPermissionRequest;
import com.example.ManagerApp.admin.services.dto.request.TogglePermissionRequest;
import com.example.ManagerApp.admin.services.dto.request.UpdateRolePermissionsRequest;
import com.example.ManagerApp.admin.services.dto.response.CheckPermissionResponse;
import com.example.ManagerApp.admin.services.dto.response.PermissionItemResponse;
import com.example.ManagerApp.admin.services.dto.response.RolePermissionsResponse;
import com.example.ManagerApp.admin.services.dto.response.TogglePermissionResponse;
import com.example.ManagerApp.admin.services.dto.response.UpdateRolePermissionsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/admin/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @PostMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public CheckPermissionResponse checkPermission(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CheckPermissionRequest request) {

        Boolean hasPermission =
                adminPermissionService.checkPermission(
                        jwt,
                        request.getPermissionName(),
                        request.getActionName()
                );

        return new CheckPermissionResponse(
                hasPermission
        );
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAuthority('roles:UPDATE')")
    public TogglePermissionResponse togglePermission(
            @RequestBody TogglePermissionRequest request
    ) {

        return adminPermissionService.togglePermission(request);
    }


    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('permissions:VIEW')")
    public List<RolePermissionsResponse> getAllRolePermissions() {

        return adminPermissionService.getAllRolePermissions();
    }

    @GetMapping("/me")
    public ResponseEntity<List<PermissionItemResponse>> getMyPermissions(
        @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(
                adminPermissionService
                        .getCurrentPermissions(jwt)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('permissions:UPDATE')")
    public UpdateRolePermissionsResponse updateRolePermissions(
            @RequestBody UpdateRolePermissionsRequest request
    ) {

        return adminPermissionService
                .updateRolePermissions(request);
    }
}