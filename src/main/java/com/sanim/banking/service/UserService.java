package com.sanim.banking.service;

import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository users;

    public User createUser(String email, String displayName, String passwordHash) {
        User u = User.builder().email(email).passwordHash("hash-1").displayName(displayName).build();
        users.save(u);
        return u;
    }
}
