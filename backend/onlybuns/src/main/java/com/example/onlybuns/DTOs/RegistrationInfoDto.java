package com.example.onlybuns.DTOs;

import com.example.onlybuns.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@PasswordMatches(message = "Passwords do not match.")
public class RegistrationInfoDto {

    @NotBlank(message = "Enter your email.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Enter a valid email address.")
    private String email;

    @NotBlank(message = "Enter your username.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._]*[a-zA-Z0-9]$",
            message = "Username must start with a letter and contain only letters, digits, dots or underscores.")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters.")
    private String username;

    @NotBlank(message = "Enter your password.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!?@#$%^&*><:;,.()]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;


    @NotBlank(message = "Re-enter your password.")
    @Size(min = 8, message = "Confirm password must be at least 8 characters.")
    private String confirmPassword;

    @NotBlank(message = "Enter your name.")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$",
            message = "Only letters, hyphens, apostrophes and spaces allowed.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @NotBlank(message = "Enter your lastname.")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$",
            message = "Only letters, hyphens, apostrophes, and spaces allowed.")
    @Size(min = 2, max = 50, message = "Lastname must be between 2 and 50 characters.")
    private String lastname;

    @NotBlank(message = "Enter your address.")
    @Pattern(regexp = "^[A-Za-z0-9À-ÖØ-öø-ÿ,.\\-\\/\\s]+$",
            message = "Invalid address format.")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters.")
    private String address;
}
