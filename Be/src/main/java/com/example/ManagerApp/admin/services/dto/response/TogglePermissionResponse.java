package com.example.ManagerApp.admin.services.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TogglePermissionResponse {

    private String roleName;
    private String permissionName;
    private String actionName;

    // true = đã thêm
    // false = đã xoá
    private Boolean enabled;
}