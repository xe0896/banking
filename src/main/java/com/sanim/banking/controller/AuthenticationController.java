package com.sanim.banking.controller;

import com.sanim.banking.config.JwtProperties;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.dto.*;
import com.sanim.banking.exception.UserNotActiveException;
import com.sanim.banking.service.JwtService;
import com.sanim.banking.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.sanim.banking.config.SecurityConfig.passwordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService users;
    private final JwtService jwt;

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        User u = users.createUser(req.email(), req.password(), req.displayName());
        return ResponseEntity.status(201).body(new RegisterResponse(u.getId()));
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest req) {
        String email = req.email();
        String password = req.password();

        PasswordEncoder encoder = passwordEncoder();
        try {
            User user = users.getUserByEmail(email);
            String token = jwt.getToken(user.getId());
            encoder.matches(password, user.getPasswordHash());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new LoginResponse(user.getDisplayName(), user.getId(), token));
        } catch (UserNotActiveException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/me")
    ResponseEntity<MeResponse> me(@RequestBody MeRequest req) {
        String token = req.token();
        SecretKey key = jwt.getKey();

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            User u = users.getUserById(userId);

            return ResponseEntity.status(HttpStatus.OK).
                    body(new MeResponse(u.getId(), u.getDisplayName(), u.getStatus(), u.getCreatedAt()));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
