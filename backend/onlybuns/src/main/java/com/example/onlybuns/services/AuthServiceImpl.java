package com.example.onlybuns.services;

import com.example.onlybuns.DTOs.JwtAuthenticationRequest;
import com.example.onlybuns.DTOs.RegistrationInfoDto;
import com.example.onlybuns.DTOs.UserTokenState;
import com.example.onlybuns.exceptions.UsernameAlreadyExistsException;
import com.example.onlybuns.models.ERole;
import com.example.onlybuns.models.User;
import com.example.onlybuns.repositories.UserRepository;
import com.example.onlybuns.services.interfaces.AuthenticationService;
import com.example.onlybuns.utility.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public UserTokenState login(JwtAuthenticationRequest loginDto) {
        Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Incorrect credentials!");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserTokenState tokenDTO = new UserTokenState();
        tokenDTO.setAccessToken(jwt);
        tokenDTO.setExpiresIn(10000000L);

        return tokenDTO;
    }

    public User register(RegistrationInfoDto registrationInfo){

        if(userRepository.findByUsername(registrationInfo.getUsername()).isPresent())
            throw new UsernameAlreadyExistsException("Username already exists: " + registrationInfo.getUsername());

        User u = new User();
        u.setUsername(registrationInfo.getUsername());
        u.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        u.setRole(ERole.ROLL_USER);

        return userRepository.save(u);
    }


}
