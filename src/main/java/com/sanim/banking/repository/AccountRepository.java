package com.sanim.banking.repository;

import com.sanim.banking.domain.account.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumberValue(String accountNumberValue);
    Optional<Account> findByOwnerUserId(UUID id);

    // The idea behind this is for the query includes a FOR UPDATE clause which means that
    // every row is locked before the current transaction is done, it releases allowing the waiting
    // queries to go ahead, ensuring that we don't bump into the case where balance > 0 then two
    // updates occur such as withdrawal or transferring and make balance negative, which could of been
    // handled if we made it properly wait. Pessimistic meaning it assumes worst case and always lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional <Account> findWithLockById (@Param("id") UUID id );
}
