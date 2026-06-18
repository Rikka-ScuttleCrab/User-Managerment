package com.example.ManagerApp.service.dto.response;
import java.util.List;
import com.example.ManagerApp.domain.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class MeResponse {
    private Long id;
    private String username;
    private String email;
    private Gender gender;
    private String nickname;
    private List<String> roles;
}