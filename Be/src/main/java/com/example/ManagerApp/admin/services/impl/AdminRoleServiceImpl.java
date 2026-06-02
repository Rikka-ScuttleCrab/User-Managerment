package com.example.ManagerApp.admin.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ManagerApp.admin.services.AdminRoleService;
import com.example.ManagerApp.admin.services.dto.request.AdminCreateRoleRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminRoleResponse;
import com.example.ManagerApp.app.rest.error.BadRequestAlertException;
import com.example.ManagerApp.dao.RoleDAO;
import com.example.ManagerApp.dao.UserDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    @Override
    public List<AdminRoleResponse> getAllRoles(Long currentUserId) {
        User currentUser = userDAO.findById(currentUserId)
            .orElseThrow(() ->
                    new BadRequestAlertException(
                            "Current user not found"
                    ));

        boolean isAdmin =
                currentUser.getRoles()
                        .stream()
                        .anyMatch(role ->
                                "ADMIN".equalsIgnoreCase(
                                        role.getRoleName()
                                )
                        );
        List<Role> roles = roleDAO.findAll();

        if (!isAdmin) {
            roles = roles.stream()
                    .filter(role ->
                            !"ADMIN".equalsIgnoreCase(
                                    role.getRoleName()
                            )
                    )
                    .toList();
        }
        
        return roles.stream().map(role ->{

            AdminRoleResponse res = new AdminRoleResponse();

            res.setRoleId(role.getId());

            res.setRoleName(role.getRoleName());

            res.setDescription(role.getDescription());

            return res;

        }).toList();
    }

    @Override
    public Role createRole(AdminCreateRoleRequest request) {

        String roleName = request.getRoleName();
        String description = request.getDescription();

        if (roleName == null || roleName.trim().isEmpty()) {
            throw new BadRequestAlertException("Role name must not be empty");
        }

        roleName = roleName.trim();

        roleDAO.findByRoleName(roleName)
                .ifPresent(role -> {
                    throw new BadRequestAlertException("Role already exists");
                });

        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription("");

        if(description != null)
            role.setDescription(description);
        

        return roleDAO.save(role);
    }

    @Override
    public AdminRoleResponse updateRole(Long id, AdminCreateRoleRequest request){

        String roleName = request.getRoleName();
        String description = request.getDescription();


        Role role = roleDAO.findById(id).orElseThrow(() ->
                        new BadRequestAlertException("Role not found"));

        if(role.getRoleName() == null ? roleName != null : !role.getRoleName().equals(roleName))
            roleDAO.findByRoleName(roleName)
                    .ifPresent(roles -> {
                        throw new BadRequestAlertException("Role already exists");
                    });

        
        if ("ADMIN".equalsIgnoreCase(role.getRoleName())
                || "READER".equalsIgnoreCase(role.getRoleName())) {

            throw new BadRequestAlertException(
                    "Cannot edit system role: " + role.getRoleName()
            );
        }

        if (request.getRoleName() != null) {
            role.setRoleName(roleName);
        }

        if (description != null && !description.trim().isEmpty()) {
            role.setDescription(description.trim());
        }

        roleDAO.update(role);

        return new AdminRoleResponse(
            role.getId(),
            role.getRoleName(),
            role.getDescription()
        );
    }

    @Override
    public void assignUsersToRole(Long currentUserId, Long roleId, List<Long> userIds) {

        Role role = roleDAO.findById(roleId)
                .orElseThrow(() ->
                        new BadRequestAlertException(
                                "Role not found"
                ));
                
        if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {

                throw new BadRequestAlertException(
                        "Cannot add users to ADMIN role"
                );
        }            

        for (Long userId : userIds) {

            userDAO.findById(userId)
                    .orElseThrow(() ->
                            new BadRequestAlertException(
                                    "User not found: " + userId
                            ));
        }

        userDAO.assignUsersToRole(
                roleId,
                userIds
        );
    }

    @Override
    public void deleteRole(Long id) {

        Role role = roleDAO.findById(id)
                .orElseThrow(() ->
                        new BadRequestAlertException(
                                "Role not found"
                        ));
        String roleName = role.getRoleName();

        // BLOCK DELETE
        if ("ADMIN".equalsIgnoreCase(roleName)
                || "READER".equalsIgnoreCase(roleName)) {

            throw new BadRequestAlertException(
                    "Cannot delete system role: " + roleName
            );
        }
        roleDAO.deleteById(id);
    }
}