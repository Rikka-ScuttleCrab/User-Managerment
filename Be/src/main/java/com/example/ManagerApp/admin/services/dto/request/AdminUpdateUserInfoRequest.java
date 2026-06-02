package com.example.ManagerApp.admin.services.dto.request;

import com.example.ManagerApp.domain.enumeration.Gender;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserInfoRequest {

    @Email
    private String email;

    private Gender gender;

    private String nickname;
}