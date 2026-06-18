package com.example.ManagerApp.admin.services.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleResponse {
    private Long roleId;
    private String roleName;
    private String description;
}
