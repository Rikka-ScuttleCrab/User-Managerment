package com.example.ManagerApp.admin.services.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckPermissionResponse {

    private Boolean hasPermission;
}