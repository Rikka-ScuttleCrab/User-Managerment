package com.example.ManagerApp.admin.services.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPermissionRequest {

    private String permissionName;

    private String actionName;
}