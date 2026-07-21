package com.sanim.banking.domain.account;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity // A singular row in the database
@Table(name = "accounts") // What to call the table
@Builder // Allows us to build a user via syntax in BankingApplication.java
@Getter // Provides Getter methods
@Setter // Provides Setter methods
@NoArgsConstructor // Creates a public User() {} for us so that JPA can use it
// The constructor below could be omitted this annotation if we as to uncomment this, reduces boilerplate
@AllArgsConstructor
public class Account {
    // accountId
    @Id
    @GeneratedValue
    private UUID id;

    // nullable, system accounts do not have an owner
    private UUID ownerUserId;

    // A single bank only needs an account number to identify its accounts, the sort-code is required
    // when there is multiple banks to find what bank, since this is a single bank application
    // then account number can be unique
    @Column(nullable = false, unique = true)
    private String accountNumberValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false, updatable = false)
    private Instant openedAt;

    // updatable is implicitily true
    @Column
    private Instant closedAt;

    @Version
    private long version;

    @PrePersist
    void onCreate() {
        System.out.println("Created");
        if(openedAt == null) openedAt = Instant.now();
        if(status == null) status = AccountStatus.OPEN;
    }
}
