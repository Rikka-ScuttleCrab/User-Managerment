package com.example.ManagerApp.admin.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ManagerApp.admin.services.AdminRoleService;
import com.example.ManagerApp.admin.services.dto.request.AdminCreateRoleRequest;
import com.example.ManagerApp.admin.services.dto.request.AssignUsersToRoleRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminRoleResponse;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.service.dto.response.MessageResponse;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/v1/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

     private final AdminRoleService adminRoleService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('roles:GET')")
    public List<AdminRoleResponse> getAllRoles(            
        @AuthenticationPrincipal Jwt jwt) 
    {
        Long currentUserId =
            jwt.getClaim("user_id");
        return adminRoleService.getAllRoles(currentUserId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('roles:CREATE')")
    public Role createRole(@RequestBody AdminCreateRoleRequest request) {
        return adminRoleService.createRole(request);
    }
    

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:UPDATE')")
    public AdminRoleResponse updateRole(
        @PathVariable("id") Long id, 
        @RequestBody AdminCreateRoleRequest request) 
    {
        return adminRoleService.updateRole(id, request);
    }

    @PutMapping("/add-users/{id}")
    @PreAuthorize("hasAuthority('roles:UPDATE')")
    public ResponseEntity<String>
    assignUsersToRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long roleId,

            @RequestBody
            AssignUsersToRoleRequest request
    ) {
        Long currentUserId =
            jwt.getClaim("user_id");

        adminRoleService.assignUsersToRole(
                currentUserId,
                roleId,
                request.getUserIds()
        );

        return ResponseEntity.ok(
                "Users assigned successfully"
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:DELETE')")
    public MessageResponse deleteRole(@PathVariable("id") Long id) {

        adminRoleService.deleteRole(id);

        return new MessageResponse(
                "Role deleted successfully"
        );
    }
}