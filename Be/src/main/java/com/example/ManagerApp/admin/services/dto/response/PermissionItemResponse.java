package com.example.ManagerApp.admin.services.dto.response;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
    "permissionName",
    "actionName"
})
@Getter
@Setter
@AllArgsConstructor
public class PermissionItemResponse {

    private String permissionName;

    private String permissionDescription;

    private String actionName;

    private String description;
}