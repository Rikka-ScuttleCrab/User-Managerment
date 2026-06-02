package com.example.ManagerApp.admin.services.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminUserPageResponse {

    private int page;

    private int size;

    private Long totalItems;

    private int totalPages;

    private List<AdminUserResponse> users;
}
