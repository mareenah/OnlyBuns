package com.example.onlybuns.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class JwtAuthenticationRequest {
    private String username;
    private String password;
}
