package com.example.onlybuns.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class JwtAuthenticationRequest {
    @NotBlank(message = "Enter your email.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Enter a valid email address.")
    private String email;

    @NotBlank(message = "Enter your password.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!?@#$%^&*><:;,.()]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;
}
