package com.sanim.banking.service;

import com.sanim.banking.domain.AccountNumber;
import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.account.Account;
import com.sanim.banking.domain.account.AccountStatus;
import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.domain.user.UserStatus;
import com.sanim.banking.exception.AccountNotFoundException;
import com.sanim.banking.exception.ForbiddenException;
import com.sanim.banking.exception.UserNotActiveException;
import com.sanim.banking.repository.AccountRepository;
import com.sanim.banking.repository.LedgerEntryRepository;
import com.sanim.banking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

// The idea behind the service is that it would manage the requests between the controller (HTTP) and the storage
// (repository), this means that we would call this once and we need this since the repositories are interfaces
// so we cannot create it our own, we need to let Spring do that
@Service
@RequiredArgsConstructor // Same bytecode as @AllArgsConstructor but only cares about final
public class AccountService {
    private final AccountRepository accounts;
    private final UserRepository users;
    private final LedgerEntryRepository ledger;

    // This label is for methods that would change data, the idea is whenever an update of the DB occurs we
    // may have some success but then one eventually fails, and we want to roll back. That is what transactional does
    // that is why it is required in the test-cases so that it can rollback
    @Transactional
    public Account openAccount(UUID id, AccountType type, String currency) {
        // Given for free by CrudRepository: Optional<T> findById(ID id), ensures that the user exists
        // before we create an account
        User user = users.findById(id).orElseThrow(() -> new UserNotActiveException("No such user"));
        if(user.getStatus() != UserStatus.ACTIVE) throw new UserNotActiveException("Inactive user");

        String accountNumber = AccountNumber.generate().accountNumber();
        Account account = Account.builder().
                ownerUserId(id).
                accountNumberValue(accountNumber).
                type(type).
                currencyCode(currency).
                status(AccountStatus.OPEN).
                build();

        accounts.save(account);
        return account;
    }

    @Transactional
    public Money getBalance(UUID accountId) {
        Account account = accounts.findById(accountId).orElseThrow(() -> new AccountNotFoundException("No such account"));
        BigDecimal sum = ledger.sumByAccountId(accountId);
        if(sum == null) return Money.zero(account.getCurrencyCode());

        return new Money(sum, Currency.getInstance(account.getCurrencyCode()));
    }

    @Transactional
    public Account freeze(UUID accountId, UUID callerid) {
        // A lot of controllers would need to catch this (we are being called by RestControllers here)
        // so the idea is to make a default HTTP response, see GlobalExceptionHandler

        Account account = accounts.findById(accountId).orElseThrow(() -> new AccountNotFoundException("No such account"));
        if(!account.getOwnerUserId().equals(callerid)) {
            throw new ForbiddenException("Cannot freeze another persons account");
        }

        account.setStatus(AccountStatus.FROZEN);
        return account;
    }
}
