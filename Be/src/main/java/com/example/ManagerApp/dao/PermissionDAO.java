package com.example.ManagerApp.dao;
import java.util.List;
import com.example.ManagerApp.admin.services.dto.response.RolePermissionResponse;
public interface PermissionDAO {
    Boolean hasPermission(
            String roleName,
            String permissionName,
            String actionName
    );
    void addPermissionToRole(
            String roleName,
            String permissionName,
            String actionName
    );
    void removePermissionFromRole(
            String roleName,
            String permissionName,
            String actionName
    );
    boolean existsByNameAndAction(String permissionName, String actionName);
    List<RolePermissionResponse> getAllRolePermissions();
    List<String> getPermissionsByUserId(Long userId);
    void updateRolePermissions(Long roleId, String permissionName, List<String> actions);
} 