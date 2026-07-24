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
@Transactional
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

    // Transactional also earns its stripes from threads, this is because if we was to hand a lock
    // to a thread then it would hold it until it commits, given that it is called in a @Transactional
    // method then it knows it will commit so it would wait until that happens, when it does happen then
    // it would release the lock allowing the blocked threads to continue
    @Transactional
    public Transaction withdraw(UUID accountId, UUID callerId, Money amount, UUID userId, String idempotencyKey) {
        Account account = accounts.findWithLockById(accountId).orElseThrow(() -> new AccountNotFoundException("Cannot find account"));
        if(!account.getOwnerUserId().equals(callerId)) {
            throw new ForbiddenException("Cannot freeze another persons account");
        }
        Optional<Transaction> collision = transactions.findByIdempotencyKeyAndInitiatedByUserId(idempotencyKey, userId);
        if(collision.isPresent()) return collision.get();

        // findWithlockById gives the thread the lock
        System.out.println(accountId);
        System.out.println(Thread.currentThread().getName());
        System.out.println("SOMEHOW PASSED");
        System.out.println(idempotencyKey);
        System.out.println(accountId);

        if(account.getStatus() == AccountStatus.CLOSED) throw new AccountClosedException("Account is closed");

        if(account.getStatus() == AccountStatus.FROZEN) throw new AccountFrozenException("Account is frozen");

        if(!account.getCurrencyCode().equals(amount.currency().getCurrencyCode())) throw new CurrencyMismatchException("Currencies do not match");

        Money balance = accountService.getBalance(accountId);
        System.out.println("London: " + balance.amount());
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
        if (collision.isPresent()) return collision.get();

        Account fromAccount = accounts.findWithLockById(fromAccountId).orElseThrow(() -> new AccountNotFoundException("Cannot find 'from' account"));
        Account toAccount = accounts.findWithLockById(toAccountId).orElseThrow(() -> new AccountNotFoundException("Cannot find 'to' account"));

        if (fromAccount.getStatus() == AccountStatus.CLOSED)
            throw new AccountClosedException("'From' account is closed");
        if (toAccount.getStatus() == AccountStatus.CLOSED) throw new AccountClosedException("'To' account is closed");

        if (fromAccount.getStatus() == AccountStatus.FROZEN)
            throw new AccountFrozenException("'From' account is frozen");
        if (toAccount.getStatus() == AccountStatus.FROZEN) throw new AccountFrozenException("'To' account is frozen");

        if (!fromAccount.getCurrencyCode().equals(amount.currency().getCurrencyCode()))
            throw new CurrencyMismatchException("'From' currencies do not match");
        if (!toAccount.getCurrencyCode().equals(amount.currency().getCurrencyCode()))
            throw new CurrencyMismatchException("'To' currencies do not match");

        Money fromAccountBalance = accountService.getBalance(fromAccountId);

        Money diff = fromAccountBalance.subtract(amount);

        if (diff.isNegative()) throw new
                InsufficientFundsException("Not enough money to withdraw: balance: " + fromAccountBalance.amount() + " tried to withdraw: " + amount.amount());

        Transaction transaction = Transaction.builder().
                idempotencyKey(idempotencyKey).
                type(TransactionType.TRANSFER).
                initiatedByUserId(userId).
                build();

        transaction = transactions.save(transaction);

        LedgerEntry fromEntry = LedgerEntry
                .builder().
                transactionId(transaction.getId()).
                accountId(fromAccountId).
                amount(amount.amount().negate()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        LedgerEntry toEntry = LedgerEntry.builder().
                transactionId(transaction.getId()).
                accountId(toAccountId).
                amount(amount.amount()).
                currencyCode(amount.currency().getCurrencyCode()).build();

        ledger.save(toEntry);
        ledger.save(fromEntry);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(Instant.now());

        // Hibernate automatically auto-commits the transaction change so the statements above
        // would actually change the data in the DB
        return transaction;
    }
}
