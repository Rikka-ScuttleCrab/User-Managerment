package com.example.ManagerApp.admin.services.dto.response;

import java.util.List;

import com.example.ManagerApp.domain.enumeration.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private Gender gender;

    private Boolean active;

    private List<String> roles;
}