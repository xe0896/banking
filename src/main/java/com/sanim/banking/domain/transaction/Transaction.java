package com.sanim.banking.domain.transaction;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity // A singular row in the database
@Table(name = "transactions", uniqueConstraints =
@UniqueConstraint(
        name = "uk_tx_idempotency_by_user",
        columnNames = {"idempotency_key", "initiated_by_user_id"}
    )
)
@Builder // Allows us to build a user via syntax in BankingApplication.java
@Getter // Provides Getter methods
@Setter // Provides Setter methods
@NoArgsConstructor // Creates a public User() {} for us so that JPA can use it
// The constructor below could be omitted this annotation if we as to uncomment this, reduces boilerplate
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    // An idempotencyKey is a key that is used to resolve retries, if a user was to perform a transaction
    // and the connection drops, it would be stuck since it doesn't know if it should retry again or if
    // that transaction would eventually work. The key would be provided each retry meaning if it is successful
    // and another one comes in that has the same key then this retry is rejected since it already worked,
    // it is combined with the userID since a key is unique to each user, if it was unique for each idempotencyKey
    // then users would have conflicts and reject money that isn't even about a user
    @Column(nullable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private UUID initiatedByUserId;

    // @Column is implicility assumed
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant initiatedAt;

    private Instant completedAt;

    @PrePersist
    void onCreate() {
        if (initiatedAt == null) initiatedAt = Instant.now();
        if (status == null) status = TransactionStatus.PENDING;
    }
}
