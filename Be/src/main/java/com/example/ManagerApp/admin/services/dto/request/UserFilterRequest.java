package com.example.ManagerApp.admin.services.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterRequest {

    private Integer page = 1;

    private Integer size = 30;

    private String keyword;

    private String role;

    private String gender;

    private String status;
}
