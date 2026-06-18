package com.example.ManagerApp.admin.services.dto.response;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class UpdateRolePermissionsResponse {
    private Long roleId;
    private String permissionName;
    private List<String> permissionActions;
}