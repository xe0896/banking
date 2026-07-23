package com.sanim.banking.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@ConfigurationProperties(prefix="banking.jwt")
public record JwtProperties (String secret, long ttlMinutes){

    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
