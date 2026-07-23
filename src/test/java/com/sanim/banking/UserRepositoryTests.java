package com.sanim.banking;

import com.sanim.banking.config.SecurityConfig;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sanim.banking.config.SecurityConfig.passwordEncoder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTests {
    // Tells to inject a UserRepository into the tests
    @Autowired
    UserRepository users;

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
        PasswordEncoder encoder = passwordEncoder();
        String actualHash = encoder.encode(password);
        String wrongHash = encoder.encode("rubbish");

        assertTrue(encoder.matches(password, actualHash));
        assertFalse(encoder.matches(password, wrongHash));
    }
}
