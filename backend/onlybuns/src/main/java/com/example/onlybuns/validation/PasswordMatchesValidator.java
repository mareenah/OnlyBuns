package com.example.onlybuns.validation;

import com.example.onlybuns.DTOs.RegistrationInfoDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationInfoDto> {
    @Override
    public void initialize(PasswordMatches string) {}

    @Override
    public boolean isValid(RegistrationInfoDto dto, ConstraintValidatorContext ctx) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}