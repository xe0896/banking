package com.sanim.banking.service;

import com.sanim.banking.domain.user.User;
import com.sanim.banking.exception.UserNotActiveException;
import com.sanim.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.sanim.banking.config.SecurityConfig.passwordEncoder;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository users;

    public User createUser(String email, String password, String displayName) {
        PasswordEncoder encoder = passwordEncoder();
        String passwordHash = encoder.encode(password);
        User u = User.builder().email(email).passwordHash(passwordHash).displayName(displayName).build();
        users.save(u);
        return u;
    }

    public User getUserByEmail(String email) throws UserNotActiveException {
        return users.findByEmail(email).orElseThrow(() -> new UserNotActiveException("No such user"));
    }

    public User getUserById(UUID id) throws UserNotActiveException {
        return users.findById(id).orElseThrow(() -> new UserNotActiveException("No such user"));
    }
}
