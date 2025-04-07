package com.example.onlybuns.services.interfaces;

import com.example.onlybuns.DTOs.JwtAuthenticationRequest;
import com.example.onlybuns.DTOs.RegistrationInfoDto;
import com.example.onlybuns.DTOs.UserTokenState;
import com.example.onlybuns.models.User;

public interface AuthenticationService {
    UserTokenState login(JwtAuthenticationRequest loginDto);
    User register(RegistrationInfoDto registrationInfo) throws InterruptedException;
    User verify (String verificationCode);
}
