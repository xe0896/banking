package com.sanim.banking;

import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
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

}
