package com.example.ManagerApp.admin.services;
import java.util.List;
import com.example.ManagerApp.admin.services.dto.request.AdminCreateRoleRequest;
import com.example.ManagerApp.admin.services.dto.response.AdminRoleResponse;
import com.example.ManagerApp.domain.Role;
public interface AdminRoleService {
     Role createRole(AdminCreateRoleRequest request);
     List<AdminRoleResponse> getAllRoles(Long currentUserId);
     AdminRoleResponse updateRole(Long id, AdminCreateRoleRequest request);
     void assignUsersToRole(Long currentUserId, Long roleId, List<Long> userIds);
     public void deleteRole(Long id);
}
