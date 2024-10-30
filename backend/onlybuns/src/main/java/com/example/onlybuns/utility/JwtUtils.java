package com.example.onlybuns.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.onlybuns.models.User;
import com.example.onlybuns.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private UserRepository userRepository;

    @Value("bomba")
    private String jwtSecret;

    @Value("900000") //15 minutes
    private long jwtExpirationMs;



    public String generateJwtToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return generateJwtToken(user);
    }

    public String generateJwtToken(User user){

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .withClaim("id", user.getId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
    }
    public String getUserNameFromJwtToken(String token) {
        return JWT.decode(token).getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken);
            return true;
        } catch (JWTDecodeException e) {
            return false;
        }
    }
}
