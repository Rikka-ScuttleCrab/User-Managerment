package com.example.ManagerApp.admin.controllers;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.ManagerApp.admin.services.AdminUserService;
import com.example.ManagerApp.admin.services.dto.request.AdminUpdateUserInfoRequest;
import com.example.ManagerApp.admin.services.dto.request.AssignRolesRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminUserPageResponse;
import com.example.ManagerApp.admin.services.dto.response.AdminUserResponse;
import com.example.ManagerApp.admin.services.dto.response.ToggleActiveResponse;
import com.example.ManagerApp.service.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/v1/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;
    @GetMapping("")
    @PreAuthorize("hasAuthority('users:GET')")
    public AdminUserPageResponse getAllUsers(
        @AuthenticationPrincipal Jwt jwt,  @RequestParam(name = "page", defaultValue = "1") int page) 
    {
        Long currentUserId =
            jwt.getClaim("user_id");
        return adminUserService.getAllUsers(currentUserId, page);
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('users:GET')")
    public List<AdminUserResponse> searchUsers(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(name = "keyword", required = false)
    String keyword,
    @RequestParam(name = "role", required = false)
    String role,
    @RequestParam(name = "gender", required = false)
    String gender,
    @RequestParam(name = "active", required = false)
    String status) {
        Long currentUserId =
                jwt.getClaim("user_id");
        System.out.println("Status = [" + status + "]");
        return adminUserService.searchUsers(
                currentUserId,
                keyword,
                role,
                gender,
                status
        );
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:UPDATE')")
    public AdminUserResponse updateUserInfo(
            @PathVariable("id") Long id,
            @RequestBody AdminUpdateUserInfoRequest request
    ) {
        return adminUserService.updateUserInfo(id, request);
    }
    @PutMapping("/{id}/active")
    @PreAuthorize("hasAuthority('users:ACTIVE')")
    public ResponseEntity<ToggleActiveResponse> toggleActive(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {
        Long currentUserId = jwt.getClaim("user_id");
        return ResponseEntity.ok(
                adminUserService.toggleActive(currentUserId, id)
        );
    }
    @PutMapping("/add-roles/{id}")
    @PreAuthorize("hasAuthority('users:ROLE')")
    public ResponseEntity<String> assignRolesToUser(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody
            AssignRolesRequest request
    ) {
        Long currentUserId = jwt.getClaim("user_id");
        adminUserService.assignRolesToUser(
                currentUserId,
                id,
                request.getRoleIds()
        );
        return ResponseEntity.ok(
                "Roles assigned successfully"
        );
    }
    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasAuthority('users:UPDATE')")
    public MessageResponse resetUserPassword(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable("id") Long id) {
        Long currentUserId =
                jwt.getClaim("user_id");
        return adminUserService.resetUserPassword(
                currentUserId,
                id
        );
    }
}