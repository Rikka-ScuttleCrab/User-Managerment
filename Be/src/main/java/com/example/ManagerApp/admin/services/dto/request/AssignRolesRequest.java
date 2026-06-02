package com.example.ManagerApp.admin.services.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRolesRequest {

    private List<Long> roleIds;
}