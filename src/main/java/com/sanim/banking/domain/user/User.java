package com.sanim.banking.domain.user;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity // A singular row in the database
@Table(name = "users") // What to call the table
@Builder // Allows us to build a user via syntax in BankingApplication.java
@Getter // Provides Getter methods
@Setter // Provides Setter methods
@NoArgsConstructor // Creates a public User() {} for us so that JPA can use it
// The constructor below could be omitted this annotation if we as to uncomment this, reduces boilerplate
@AllArgsConstructor
public class User {
    // Primary ID, and also let JPA (Jakarta Persistence API) to populate its value
    @Id
    @GeneratedValue
    private UUID id;

    // Column annotation defines a column, and we can provide some properties we want it to enforce
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String displayName;

    // Instead of storing the ordinal of the enum (integer) we use the actual string represented
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // The idea behind this is optimistic locking, which is when we have a version counter that increments
    // whenever a row is updated, the reason we do this is that if we had two users that update a row
    // the first user would increment the version counter and when the second user comes in, presumably right after
    // it may assume its version=1 when it is actually version=2, they would be told that the version mismatches
    // and they should try again
    @Version
    private long version;

    /*
    public User(UUID id, String email, String passwordHash, String displayName, UserStatus status, Instant createdAt, long version) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.status = status;
        this.createdAt = Instant.now();
        this.version = version;
    }
    */

    // Method on the entity that JPA calls automatically JUST BEFORE the row is inserted,
    // so it would be null upon making but then when it is in the row it would be populated
    @PrePersist
    void onCreate() {
        System.out.println("Created");
        if(createdAt == null) createdAt = Instant.now();
        if(status == null) status = UserStatus.ACTIVE;
    }

}