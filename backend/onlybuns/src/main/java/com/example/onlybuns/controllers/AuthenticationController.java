package com.example.onlybuns.controllers;

import com.example.onlybuns.DTOs.JwtAuthenticationRequest;
import com.example.onlybuns.DTOs.RegistrationInfoDto;
import com.example.onlybuns.DTOs.UserTokenState;
import com.example.onlybuns.exceptions.UsernameAlreadyExistsException;
import com.example.onlybuns.exceptions.EmailAlreadyExistsException;
import com.example.onlybuns.models.User;
import com.example.onlybuns.services.interfaces.AuthenticationService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Object> register(@RequestBody RegistrationInfoDto registrationInfoDto){
        try {
            return ResponseEntity.ok(authenticationService.register(registrationInfoDto));
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }  catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @GetMapping(value = "/verify", produces = "application/json")
    public Boolean verify(@RequestParam String verificationCode) throws Exception{
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
