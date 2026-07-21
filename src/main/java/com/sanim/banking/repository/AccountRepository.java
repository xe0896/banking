package com.sanim.banking.repository;

import com.sanim.banking.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumberValue(String accountNumberValue);
    Optional<Account> findByOwnerUserId(UUID id);
}
