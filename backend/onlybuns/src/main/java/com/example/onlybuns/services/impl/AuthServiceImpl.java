package com.example.onlybuns.services.impl;

import com.example.onlybuns.DTOs.JwtAuthenticationRequest;
import com.example.onlybuns.DTOs.RegistrationInfoDto;
import com.example.onlybuns.DTOs.UserTokenState;
import com.example.onlybuns.models.ERole;
import com.example.onlybuns.models.User;
import com.example.onlybuns.repositories.UserRepository;
import com.example.onlybuns.services.bloomfilter.UsernameBloomFilter;
import com.example.onlybuns.services.interfaces.AuthenticationService;
import com.example.onlybuns.utility.JwtUtils;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    private UsernameBloomFilter bloomFilter;

    private final static Duration VERIFICATION_LINK_EXPIRY_DURATION = Duration.ofHours(24);

    public UserTokenState loginWithRateLimit(JwtAuthenticationRequest loginDto, String ip) {
        RateLimiterConfig config = rateLimiterRegistry.rateLimiter("standard").getRateLimiterConfig();
        RateLimiter limiter = rateLimiterRegistry.rateLimiter("login-" + ip, config);

        Callable<UserTokenState> restrictedLogin = RateLimiter
                .decorateCallable(limiter, () -> login(loginDto));

        try {
            return restrictedLogin.call();
        } catch (RequestNotPermitted ex) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many login attempts from this IP address. Please try again later."
            );
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Login failed due to an unexpected error."
            );
        }
    }

    public UserTokenState login(JwtAuthenticationRequest loginDto) {
        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid input. Please check the fields.");
        }
        if(!userOpt.get().getEnabled()){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is not verified yet.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userOpt.get().getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserTokenState tokenDTO = new UserTokenState();
            tokenDTO.setAccessToken(jwt);
            tokenDTO.setExpiresIn(10000000L);

            return tokenDTO;
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials. Please try again.");
        }
    }

    public User register(RegistrationInfoDto registrationInfo) throws InterruptedException {
        if(bloomFilter.mightContain(registrationInfo.getUsername()))
            if(userRepository.findByUsername(registrationInfo.getUsername()).isPresent())
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Username \"" + registrationInfo.getUsername() + "\" already exists.");
        if (userRepository.findByEmail(registrationInfo.getEmail()).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email \"" + registrationInfo.getEmail() + "\" already exists.");

        User u = new User();
        u.setUsername(registrationInfo.getUsername());
        u.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        u.setRole(ERole.ROLL_USER);
        u.setEmail(registrationInfo.getEmail());
        u.setName(registrationInfo.getName());
        u.setLastname(registrationInfo.getLastname());
        u.setVerificationCode(UUID.randomUUID().toString().replaceAll("-", ""));
        u.setEnabled(false);
        u.setVerificationCodeCreatedAt(LocalDateTime.now());
        userRepository.save(u);

        bloomFilter.add(u.getUsername());
        sendVerificationEmail(u);

        return u;
    }

    private void sendVerificationEmail(User user) throws InterruptedException {
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "ISA team.";
        String verificationLink = "http://localhost:4200/verify/" + user.getVerificationCode();

        //Simulation of longer activity
        Thread.sleep(8000);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(user.getEmail());
            helper.setFrom(env.getProperty("spring.mail.username"));
            helper.setSubject("Please verify your registration");

            content = content.replace("[[name]]", user.getUsername());
            content = content.replace("[[URL]]", verificationLink);

            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
        System.out.println("Email sent!");
    }

    public User verify(String verificationCode){
        User user = userRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid verification link. Please register again."));

        if (user.getEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Your account is already verified."
            );
        }

        LocalDateTime createdAt = user.getVerificationCodeCreatedAt();
        if(createdAt == null || createdAt.plus(VERIFICATION_LINK_EXPIRY_DURATION).isBefore(LocalDateTime.now())){
            userRepository.deleteById(user.getId());
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "Verification link has expired. Please register again."
            );
        }

        changeUserStatus(user);
        return userRepository.save(user);
    }

    private void changeUserStatus(User user) {
        user.setEnabled(true);
    }
}
