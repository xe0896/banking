package com.sanim.banking.service;

import com.sanim.banking.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwt;

    public String getToken(UUID id) {
        SecretKey key = jwt.getKey();
        Instant expiry = Instant.now().plus(jwt.ttlMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder().subject(String.valueOf(id)).expiration(Date.from(expiry)).signWith(key).compact();
    }

    public SecretKey getKey() {
        return jwt.getKey();
    }
}
