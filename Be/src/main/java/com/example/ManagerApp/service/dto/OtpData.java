package com.example.ManagerApp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtpData {

    private String otp;

    private long createdTime;
}