package com.sanim.banking;

import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.account.Account;
import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.domain.transaction.Transaction;
import com.sanim.banking.domain.transaction.TransactionType;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.exception.InsufficientFundsException;
import com.sanim.banking.repository.LedgerEntryRepository;
import com.sanim.banking.service.AccountService;
import com.sanim.banking.repository.TransactionRepository;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TransactionRepositoryTests {
    @Autowired
    TransactionRepository transactions;
    @Autowired
    UserRepository users;
    @Autowired
    AccountService accounts;
    @Autowired
    TransactionService transactionService;
    @Autowired
    LedgerEntryRepository ledger;

    @Test
    void findByIdempotencyKeyAndInitiatedByUserIdTest() {
        UUID userId = UUID.randomUUID();

        Transaction transaction = Transaction.builder().
                idempotencyKey("idom-key").
                type(TransactionType.DEPOSIT).
                initiatedByUserId(userId).
                build();

        transactions.save(transaction);

        Transaction receivedTrans = transactions.findByIdempotencyKeyAndInitiatedByUserId("idom-key", userId)
                .orElseThrow();

        boolean res = receivedTrans.getIdempotencyKey().equals("idom-key") && receivedTrans.getInitiatedByUserId().equals(userId);

        assertTrue(res);
    }

    @Test
    void depositSumEqualZero() {
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        UUID userId = u.getId();

        Account account = accounts.openAccount(userId, AccountType.SAVINGS, "GBP");
        String currencyCode = account.getCurrencyCode();

        transactionService.deposit(account.getId(), Money.of("100.00", currencyCode), userId, "idom-1");
        transactionService.deposit(account.getId(), Money.of("200.00", currencyCode), userId, "idom-2");

        BigDecimal sum = ledger.sumWholeLedger();
        // Sum of ledger should always be zero since system account balances it out for deposits/withdrawls
        assertEquals(new BigDecimal("0.00"), sum);
    }

    @Test
    void withdrawalSumEqualZero() {
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        UUID userId = u.getId();

        Account account = accounts.openAccount(userId, AccountType.SAVINGS, "GBP");
        String currencyCode = account.getCurrencyCode();

        transactionService.deposit(account.getId(), Money.of("100.00", currencyCode), userId, "idom-1");
        transactionService.deposit(account.getId(), Money.of("200.00", currencyCode), userId, "idom-2");

        BigDecimal sum = ledger.sumWholeLedger();
        // Sum of ledger should always be zero since system account balances it out for deposits/withdrawls
        assertEquals(new BigDecimal("0.00"), sum);
    }

    @Test
    void withdrawingMoreThanBalanceThrows() {
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        Account account = accounts.openAccount(u.getId(), AccountType.CHECKING, "GBP");

        transactionService.deposit(account.getId(), Money.of("100.00", "GBP"), u.getId(), "idom-1");

        System.out.println("Count: " + ledger.count());

        long prevCount = ledger.count();

        // 1st param is the exception class we expect
        // 2nd param is the arrow function that we want to execute that should throw the exception
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdraw(account.getId(),
                Money.of("200.00", "GBP"), u.getId(), "idom-2"));

        assertEquals(prevCount, ledger.count());
    }
}
