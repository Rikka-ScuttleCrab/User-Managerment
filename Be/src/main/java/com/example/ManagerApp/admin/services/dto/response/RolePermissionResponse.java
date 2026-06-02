package com.example.ManagerApp.admin.services.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RolePermissionResponse {

    private String roleName;

    private String permissionName;

    private String permissionDescription;

    private String actionName;

    private String description;
}