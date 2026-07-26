package com.sanim.banking;

import com.sanim.banking.config.SecurityConfig;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTests {
    // Tells to inject a UserRepository into the tests
    @Autowired
    UserRepository users;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtService jwt;

    @Test
    void findByEmailTest() {
        String email = "sanimahmed26@gmail.com";
        User u = User.builder().email(email).displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        // getEmail() is required since findByEmail returns a User object
        String receivedEmail = users.findByEmail(email).get().getEmail();
        assertEquals(email, receivedEmail);
    }

    @Test
    void checkPasswordHash() {
        String password = "samsung123";
        String actualHash = encoder.encode(password);
        String wrongHash = encoder.encode("rubbish");

        assertTrue(encoder.matches(password, actualHash));
        assertFalse(encoder.matches(password, wrongHash));
    }

    @Test
    void JwtTokenTest() {
        String email = "sanimahmed26@gmail.com";
        User u = User.builder().email(email).displayName("display-name").passwordHash("password-hash").build();
        users.save(u); // CREAT TEH ID
        String token = jwt.getToken(u.getId());

        SecretKey key = jwt.getKey();

        System.out.println(u.getId());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(claims.getSubject());

        assertEquals(u.getId(), userId);

    }
}
