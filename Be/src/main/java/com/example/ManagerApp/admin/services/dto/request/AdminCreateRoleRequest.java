package com.example.ManagerApp.admin.services.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateRoleRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;
}