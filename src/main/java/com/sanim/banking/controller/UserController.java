package com.sanim.banking.controller;

import com.sanim.banking.domain.user.User;
import com.sanim.banking.dto.UserRequest;
import com.sanim.banking.dto.UserResponse;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService us    ers;

    @PostMapping
    ResponseEntity<UserResponse> createUser(@RequestBody UserRequest req) {
        User u = users.createUser(req.email(), req.displayName(), req.passwordHash());
        return ResponseEntity.status(201).body(toDto(u));
    }

    private UserResponse toDto(User u) {
        return new UserResponse(u.getId());
    }
}
