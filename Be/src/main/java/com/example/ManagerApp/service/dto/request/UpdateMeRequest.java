package com.example.ManagerApp.service.dto.request;
import com.example.ManagerApp.domain.enumeration.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UpdateMeRequest {
    @Email
    private String email;
    @Size(min = 8, max = 60)
    private String password;
    private Gender gender;
    @Size(min = 2, max = 256)
    private String nickname;
}