package com.sanim.banking.repository;

import com.sanim.banking.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    // The idea behind this is that we do not want this to return NULL as it could be asked for an email
    // that doesn't exist so it returns NULL and then if we was to just make this return User instead of
    // Optional<User> then we could have a world where we try to do u.id ... and since u is null then it would
    // be a runtime error, instead we want it to be a compile time error so we must unwrap and acknowledge
    // that it can be null and handle it, the main pattern would to use orElseThrow(() -> new UserNotActiveException("No such user")

    // The findBy prefix suggest a query derivation from Spring meaning it would do a SELECT statement
    // findByEmail(String) → SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Optional not required since its the same thing as the list being empty when it returns nothing, i.e null
    @Query("select u.email from User u")
    List<String> getAllEmails();
}
