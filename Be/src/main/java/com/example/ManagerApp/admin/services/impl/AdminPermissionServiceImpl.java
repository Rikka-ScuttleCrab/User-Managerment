package com.example.ManagerApp.admin.services.impl;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import com.example.ManagerApp.admin.services.AdminPermissionService;
import com.example.ManagerApp.admin.services.dto.request.TogglePermissionRequest;
import com.example.ManagerApp.admin.services.dto.request.UpdateRolePermissionsRequest;
import com.example.ManagerApp.admin.services.dto.response.PermissionItemResponse;
import com.example.ManagerApp.admin.services.dto.response.RolePermissionResponse;
import com.example.ManagerApp.admin.services.dto.response.RolePermissionsResponse;
import com.example.ManagerApp.admin.services.dto.response.TogglePermissionResponse;
import com.example.ManagerApp.admin.services.dto.response.UpdateRolePermissionsResponse;
import com.example.ManagerApp.app.rest.error.BadRequestAlertException;
import com.example.ManagerApp.dao.PermissionDAO;
import com.example.ManagerApp.dao.RoleDAO;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AdminPermissionServiceImpl
        implements AdminPermissionService {
    private final PermissionDAO permissionDAO;
    private final RoleDAO roleDAO;
    @Override
    public boolean checkPermission(
                Jwt jwt,
                String permissionName,
                String actionName) {
        // check permission có tồn tại trong DB không
        boolean permissionExists =
                permissionDAO.existsByNameAndAction(
                        permissionName,
                        actionName
                );
        if (!permissionExists) {
                throw new BadRequestAlertException(
                        "Permission does not exist"
                );
        }
        List<String> permissions =
                jwt.getClaim("permission");
        if (permissions == null) {
                return false;
        }
        String targetPermission =
                permissionName + ":" + actionName;
                return permissions.contains(targetPermission);
    }
    @Override
    public TogglePermissionResponse togglePermission(
            TogglePermissionRequest request
    ) {
        boolean exists = permissionDAO.hasPermission(
                request.getRoleName(),
                request.getPermissionName(),
                request.getActionName()
        );
        if (exists) {
            permissionDAO.removePermissionFromRole(
                    request.getRoleName(),
                    request.getPermissionName(),
                    request.getActionName()
            );
            return new TogglePermissionResponse(
                    request.getRoleName(),
                    request.getPermissionName(),
                    request.getActionName(),
                    false
            );
        }
        permissionDAO.addPermissionToRole(
                request.getRoleName(),
                request.getPermissionName(),
                request.getActionName()
        );
        return new TogglePermissionResponse(
                request.getRoleName(),
                request.getPermissionName(),
                request.getActionName(),
                true
        );
    }
    @Override
    public List<RolePermissionsResponse> getAllRolePermissions() {
        List<RolePermissionResponse> rows =
                permissionDAO.getAllRolePermissions();
        Map<String, List<PermissionItemResponse>>
                groupedPermissions = new LinkedHashMap<>();
        for (RolePermissionResponse row : rows) {
                groupedPermissions
                        .computeIfAbsent(
                                row.getRoleName(),
                                k -> new ArrayList<>()
                        )
                        .add(
                                new PermissionItemResponse(
                                        row.getPermissionName(),
                                        row.getPermissionDescription(),
                                        row.getActionName(),
                                        row.getDescription()
                                )
                        );
        }
        List<RolePermissionsResponse> result =
                new ArrayList<>();
        for (
                Map.Entry<
                        String,
                        List<PermissionItemResponse>
                > entry
                : groupedPermissions.entrySet()
        ) {
                RolePermissionsResponse response =
                        new RolePermissionsResponse();
                response.setRoleName(entry.getKey());
                response.setPermissions(entry.getValue());
                result.add(response);
        }
        return result;
    }
    @Override
    public List<PermissionItemResponse> getCurrentPermissions(Jwt jwt) {
        List<String> permissions =
                jwt.getClaimAsStringList("permission");
        return permissions.stream()
                .map(permission -> {
                String[] parts =
                        permission.split(":");
                return new PermissionItemResponse(
                        parts[0],
                        null,
                        parts[1],
                        null
                );
        }) .toList();
    }
        @Override
        public UpdateRolePermissionsResponse updateRolePermissions(
                UpdateRolePermissionsRequest request
        ) {
        roleDAO.findById(request.getRoleId())
                .orElseThrow(() ->
                        new BadRequestAlertException(
                                "Role not found"
                        ));
        // validate permission exists
        for (String action : request.getPermissionActions()) {
                boolean exists =
                        permissionDAO.existsByNameAndAction(
                                request.getPermissionName(),
                                action
                        );
                if (!exists) {
                throw new BadRequestAlertException(
                        "Permission not found: "
                        + request.getPermissionName()
                        + ":" + action
                );
                }
        }
        permissionDAO.updateRolePermissions(
                request.getRoleId(),
                request.getPermissionName(),
                request.getPermissionActions()
        );
        return new UpdateRolePermissionsResponse(
                request.getRoleId(),
                request.getPermissionName(),
                request.getPermissionActions()
        );
        }
}