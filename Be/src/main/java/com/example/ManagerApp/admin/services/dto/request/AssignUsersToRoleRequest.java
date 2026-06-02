package com.example.ManagerApp.admin.services.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignUsersToRoleRequest {

    private List<Long> userIds;
}