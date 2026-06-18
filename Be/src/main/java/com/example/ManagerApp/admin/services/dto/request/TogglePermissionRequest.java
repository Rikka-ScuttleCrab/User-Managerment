package com.example.ManagerApp.admin.services.dto.request;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class TogglePermissionRequest {
    private String roleName;
    private String permissionName;
    private String actionName;
}