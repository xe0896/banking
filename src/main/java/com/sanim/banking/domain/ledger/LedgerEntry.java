package com.sanim.banking.domain.ledger;

import com.sanim.banking.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity // A singular row in the database
@Table(name = "ledger", indexes = {
        @Index(name = "ix_ledger_account", columnList = "account_id"),
        @Index(name = "ix_ledger_transaction", columnList = "transaction_id")
})
@Builder // Allows us to build a user via syntax in BankingApplication.java
@Getter // Provides Getter methods
@Setter // Provides Setter methods
@NoArgsConstructor // Creates a public User() {} for us so that JPA can use it
// The constructor below could be omitted this annotation if we as to uncomment this, reduces boilerplate
@AllArgsConstructor
public class LedgerEntry {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID transactionId;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private Instant postedAt;

    @PrePersist
    void onCreate() {
        if(postedAt == null) postedAt = Instant.now();
    }

    public Money money() {
        return new Money(amount, Currency.getInstance(currencyCode));
    }
}
