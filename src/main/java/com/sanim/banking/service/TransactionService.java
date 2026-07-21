package com.sanim.banking.service;

import com.sanim.banking.config.SystemAccountNumbers;
import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.account.Account;
import com.sanim.banking.domain.account.AccountStatus;
import com.sanim.banking.domain.ledger.LedgerEntry;
import com.sanim.banking.domain.transaction.Transaction;
import com.sanim.banking.domain.transaction.TransactionStatus;
import com.sanim.banking.domain.transaction.TransactionType;
import com.sanim.banking.exception.*;
import com.sanim.banking.repository.AccountRepository;
import com.sanim.banking.repository.LedgerEntryRepository;
import com.sanim.banking.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.sanim.banking.config.SystemAccountNumbers.getUUIDSystemId;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactions;
    private final AccountRepository accounts;
    private final LedgerEntryRepository ledger;
    private final AccountService accountService;

    // accountId is random but changes per account, so we provide it and see it as a primary key
    @Transactional
    public Transaction deposit(UUID accountId, Money amount, UUID userId, String idempotencyKey) {
        Optional<Transaction> collision = transactions.findByIdempotencyKeyAndInitiatedByUserId(idempotencyKey, userId);
        if(collision.isPresent()) return collision.get();

        Account account = accounts.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Cannot find account"));

        if(account.getStatus() == AccountStatus.CLOSED) throw new AccountClosedException("Account is closed");

        if(account.getStatus() == AccountStatus.FROZEN) throw new AccountFrozenException("Account is frozen");

        if(!account.getCurrencyCode().equals(amount.currency().getCurrencyCode())) throw new CurrencyMismatchException("Currencies do not match");

        Transaction transaction = Transaction.builder().
                idempotencyKey(idempotencyKey).
                type(TransactionType.DEPOSIT).
                initiatedByUserId(userId).
                build();

        transaction = transactions.save(transaction); // Now it is in the DB, it now has a ID field, save returns what it saved

        LedgerEntry userEntry = LedgerEntry
                .builder().
                transactionId(transaction.getId()).
                accountId(accountId).
                amount(amount.amount()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        UUID systemAccountId = getUUIDSystemId(SystemAccountNumbers.CASH_IN, accounts);

        LedgerEntry systemEntry = LedgerEntry.builder().
                transactionId(transaction.getId()).
                accountId(systemAccountId).
                amount(amount.amount().negate()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        ledger.save(userEntry);
        ledger.save(systemEntry);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(Instant.now());

        // Hibernate automatically auto-commits the transaction change so the statements above
        // would actually change the data in the DB
        return transaction;
    }

    @Transactional
    public Transaction withdraw(UUID accountId, Money amount, UUID userId, String idempotencyKey) {
        Optional<Transaction> collision = transactions.findByIdempotencyKeyAndInitiatedByUserId(idempotencyKey, userId);
        if(collision.isPresent()) return collision.get();

        Account account = accounts.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Cannot find account"));

        if(account.getStatus() == AccountStatus.CLOSED) throw new AccountClosedException("Account is closed");

        if(account.getStatus() == AccountStatus.FROZEN) throw new AccountFrozenException("Account is frozen");

        if(!account.getCurrencyCode().equals(amount.currency().getCurrencyCode())) throw new CurrencyMismatchException("Currencies do not match");

        Money balance = accountService.getBalance(accountId);
        Money diff = balance.subtract(amount);

        if(diff.isNegative()) throw new
                InsufficientFundsException("Not enough money to withdraw: balance: " + balance.amount() + " tried to withdraw: " + amount.amount());

        Transaction transaction = Transaction.builder().
                idempotencyKey(idempotencyKey).
                type(TransactionType.WITHDRAWAL).
                initiatedByUserId(userId).
                build();

        transaction = transactions.save(transaction); // Now it is in the DB, it now has a ID field, save returns what it saved

        LedgerEntry userEntry = LedgerEntry
                .builder().
                transactionId(transaction.getId()).
                accountId(accountId).
                amount(amount.amount().negate()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        UUID systemAccountId = getUUIDSystemId(SystemAccountNumbers.CASH_OUT, accounts);

        LedgerEntry systemEntry = LedgerEntry.builder().
                transactionId(transaction.getId()).
                accountId(systemAccountId).
                amount(amount.amount()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        ledger.save(userEntry);
        ledger.save(systemEntry);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(Instant.now());

        // Hibernate automatically auto-commits the transaction change so the statements above
        // would actually change the data in the DB
        return transaction;
    }

    @Transactional
    public Transaction transfer(UUID fromAccountId, UUID toAccountId, Money amount, UUID userId, String idempotencyKey) {
        // The userId is owned by the destination so from account
        Optional<Transaction> collision = transactions.findByIdempotencyKeyAndInitiatedByUserId(idempotencyKey, userId);
        if(collision.isPresent()) return collision.get();

    }
}
