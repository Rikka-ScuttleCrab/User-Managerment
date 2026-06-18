package com.example.ManagerApp.app.rest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ManagerApp.service.UserService;
import com.example.ManagerApp.service.dto.request.ChangePasswordRequest;
import com.example.ManagerApp.service.dto.request.UpdateMeRequest;
import com.example.ManagerApp.service.dto.response.MeResponse;
import com.example.ManagerApp.service.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeResponse> getMe(
            @AuthenticationPrincipal Jwt jwt) {
        Long currentUserId =
                Long.valueOf(jwt.getClaim("user_id").toString());
        return ResponseEntity.ok(
                userService.getMe(currentUserId));
    }
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeResponse> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateMeRequest request) {
        Long currentUserId = 
            Long.valueOf(jwt.getClaim("user_id").toString());
        return ResponseEntity.ok(
                userService.updateMe(currentUserId, request));
    }
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest request) {
    Long currentUserId =
            Long.valueOf(jwt.getClaim("user_id").toString());
    return ResponseEntity.ok(
            userService.changePassword(currentUserId, request));
    }
}