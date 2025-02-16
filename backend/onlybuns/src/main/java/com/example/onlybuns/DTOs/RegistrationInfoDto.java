package com.example.onlybuns.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class RegistrationInfoDto {
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String name;
    private String lastname;
    private String address;
}
