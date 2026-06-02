package com.example.ManagerApp.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @NotBlank
    private String login;

    @NotBlank
    private String password;
}