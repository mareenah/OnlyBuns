package com.example.onlybuns.controllers;

import com.example.onlybuns.DTOs.JwtAuthenticationRequest;
import com.example.onlybuns.DTOs.RegistrationInfoDto;
import com.example.onlybuns.DTOs.UserTokenState;
import com.example.onlybuns.models.User;
import com.example.onlybuns.services.interfaces.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> login(HttpServletRequest request, @RequestBody JwtAuthenticationRequest loginDto){
        return ResponseEntity.ok(authenticationService.loginWithRateLimit(loginDto, request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegistrationInfoDto registrationInfoDto) throws InterruptedException {
        return ResponseEntity.ok(authenticationService.register(registrationInfoDto));
    }

    @GetMapping(value = "/verify", produces = "application/json")
    public Boolean verify(@RequestParam String verificationCode) {
        User verifiedUser = authenticationService.verify(verificationCode);
        if(verifiedUser == null)
            throw new RuntimeException();
        return true;
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLL_USER')")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Authentication works!.");
    }

}
