package com.example.ManagerApp.admin.services.impl;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ManagerApp.admin.services.AdminUserService;
import com.example.ManagerApp.admin.services.dto.request.AdminUpdateUserInfoRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminUserPageResponse;
import com.example.ManagerApp.admin.services.dto.response.AdminUserResponse;
import com.example.ManagerApp.admin.services.dto.response.ToggleActiveResponse;
import com.example.ManagerApp.app.rest.error.BadRequestAlertException;
import com.example.ManagerApp.dao.RoleDAO;
import com.example.ManagerApp.dao.UserDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.service.dto.response.MessageResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;
        private static final Pattern EMAIL_PATTERN =
        Pattern.compile(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    @Override
    public AdminUserPageResponse getAllUsers(Long currentUserId, int page) {
        int size = 7;
        User currentUser = userDAO.findById(currentUserId)
                .orElseThrow(()
                        -> new BadRequestAlertException(
                        "Current user not found"
                ));
        boolean isAdmin
                = currentUser.getRoles()
                        .stream()
                        .anyMatch(role
                                -> "ADMIN".equalsIgnoreCase(
                                role.getRoleName()
                        ));
        List<User> users = userDAO.findAll(
                page,
                size,
                isAdmin
        );
        long totalItems
                = userDAO.countUsers(isAdmin);
        int totalPages
                = (int) Math.ceil(
                        (double) totalItems / size);
        List<AdminUserResponse> data
                = users.stream()
                        .map(user -> {
                            AdminUserResponse res
                                    = new AdminUserResponse();
                            res.setId(user.getId());
                            res.setUsername(user.getUsername());
                            res.setNickname(user.getNickname());
                            res.setEmail(user.getEmail());
                            res.setGender(user.getGender());
                            res.setActive(user.getActive());
                            res.setRoles(
                                    user.getRoles()
                                            .stream()
                                            .map(Role::getRoleName)
                                            .toList()
                            );
                            return res;
                        })
                        .toList();
        return new AdminUserPageResponse(
                page,
                size,
                totalItems,
                totalPages,
                data
        );
    }
    @Override
    public AdminUserResponse updateUserInfo(
            Long id,
            AdminUpdateUserInfoRequest request
    ) {
        User user = userDAO.findById(id)
                .orElseThrow(()
                        -> new BadRequestAlertException("User not found"));
        // update email
        if (request.getEmail() != null) {

        String email = request.getEmail().trim();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new BadRequestAlertException(
                        "Invalid email format"
                );
        }

        userDAO.findByEmail(email)
                .ifPresent(u -> {
                        if (!u.getId().equals(user.getId())) {
                        throw new BadRequestAlertException(
                                "Email already exists"
                        );
                        }
                });

        user.setEmail(email);
        }
        // update gender
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        // update nickname
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        userDAO.updateInfo(user);
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getGender(),
                user.getActive(),
                user.getRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .toList()
        );
    }
    @Override
    public ToggleActiveResponse toggleActive(Long currentUserId, Long id) {
        if (currentUserId.equals(id)) {
            throw new BadRequestAlertException(
                    "Cannot change your own active status");
        }
        User user = userDAO.findById(id)
                .orElseThrow(()
                        -> new BadRequestAlertException("User not found"));
        Boolean newActive = !user.getActive();
        userDAO.updateActive(id, newActive);
        return new ToggleActiveResponse(
                user.getId(),
                newActive
        );
    }
    @Override
    public void assignRolesToUser(Long currentUserId, Long userId, List<Long> roleIds) {
        // Check if the current user has permission to assign roles
        User currentUser = userDAO.findById(currentUserId)
                .orElseThrow(()
                        -> new BadRequestAlertException("Current user not found"));
        boolean isAdmin
                = currentUser.getRoles()
                        .stream()
                        .anyMatch(role
                                -> "ADMIN".equalsIgnoreCase(
                                role.getRoleName()
                        ));
        userDAO.findById(userId)
                .orElseThrow(()
                        -> new BadRequestAlertException(
                        "User not found"
                ));
        for (Long roleId : roleIds) {
            Role role = roleDAO.findById(roleId)
                    .orElseThrow(()
                            -> new BadRequestAlertException(
                            "Role not found: " + roleId
                    ));
            if(!isAdmin){            
                if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {
                        throw new BadRequestAlertException(
                                "Cannot add users to ADMIN role"
                        );
                }
            }
        }
        userDAO.assignRolesToUser(
                userId,
                roleIds
        );
    }
    @Override
    public MessageResponse resetUserPassword(
            Long currentUserId,
            Long targetUserId
    ) {
        // không cho reset chính mình
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestAlertException(
                    "Cannot reset your own password"
            );
        }
        User user = userDAO.findById(targetUserId)
                .orElseThrow(()
                        -> new BadRequestAlertException(
                        "User not found"
                ));
        String defaultPassword = "News2106";
        String encodedPassword
                = passwordEncoder.encode(defaultPassword);
        userDAO.updatePassword(
                user.getId(),
                encodedPassword
        );
        return new MessageResponse(
                "Password reset successfully"
        );
    }
    @Override
    public List<AdminUserResponse> searchUsers(
            Long currentUserId,
            String keyword,
            String role,
            String gender,
            String status
    ) {
        User currentUser = userDAO.findById(currentUserId)
                .orElseThrow(()
                        -> new BadRequestAlertException(
                        "Current user not found"
                ));
        boolean isAdmin
                = currentUser.getRoles()
                        .stream()
                        .anyMatch(r
                                -> "ADMIN".equalsIgnoreCase(
                                r.getRoleName()
                        )
                        );
        List<User> users
                = userDAO.searchUsers(
                        keyword,
                        role,
                        gender,
                        status,
                        isAdmin
                );
        return users.stream()
                .map(user -> {
                    AdminUserResponse res
                            = new AdminUserResponse();
                    res.setId(user.getId());
                    res.setUsername(user.getUsername());
                    res.setNickname(user.getNickname());
                    res.setEmail(user.getEmail());
                    res.setGender(user.getGender());
                    res.setActive(user.getActive());
                    res.setRoles(
                            user.getRoles()
                                    .stream()
                                    .map(Role::getRoleName)
                                    .toList()
                    );
                    return res;
                })
                .toList();
    }
}
