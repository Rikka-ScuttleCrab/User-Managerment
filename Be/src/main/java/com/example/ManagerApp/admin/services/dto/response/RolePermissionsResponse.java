package com.example.ManagerApp.admin.services.dto.response;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
    "roleName",
    "permissions"
})
@Getter
@Setter
public class RolePermissionsResponse {

    private String roleName;

    private List<PermissionItemResponse> permissions;
}