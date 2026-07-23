package com.sanim.banking;

import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.account.Account;
import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.exception.InsufficientFundsException;
import com.sanim.banking.repository.AccountRepository;
import com.sanim.banking.repository.LedgerEntryRepository;
import com.sanim.banking.repository.TransactionRepository;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.AccountService;
import com.sanim.banking.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static aQute.bnd.annotation.headers.Category.users;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyTests {
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
    @Autowired
    AccountRepository accountsRepo;

    // No transactional since as we save the before commit measures would store in cache rather than
    // actual DB so the threads won't be able to find the account if we was to let it in cache,
    // so remove the transactional so it actually populates the DB right there and then
    @Test
    void TenThreadsWithdrawing() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10); // Required, decremented until latch.await finds it to be 0
        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        // The CountDownLatch is required since if we was to increment then read the current value, it may
        // be about to be incremented but then they are looking at the stale value so it cannot overwrite the old + 1

        // AtomicInteger would do incrementAndGet() whenever a thread succeeds/fails

        System.out.println(users.getAllEmails());
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        Account account = accounts.openAccount(u.getId(), AccountType.CHECKING, "GBP");

        ExecutorService service = Executors.newFixedThreadPool(10);

        transactionService.deposit(account.getId(),
                Money.of("100.00", "GBP"), u.getId(), String.valueOf(UUID.randomUUID()));
        for(int i = 0; i < 10; i++) {
            UUID accountId = account.getId();
            UUID userId = u.getId();
            service.execute(() -> {
                    try {
                        transactionService.withdraw(accountId, accountId,
                                Money.of("20.00", "GBP"), userId, String.valueOf(UUID.randomUUID()));

                        succeeded.incrementAndGet();
                    } catch (InsufficientFundsException e) {
                        System.out.println("BLACK TERY BLACK AKAC: " + failed.get());
                        failed.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
            });
        }

        // This await() would make all the threads need to finish before the main thread continues
        // and it can find out until latch is 0 from the 10 latch.countDown() calls
        latch.await();
        service.shutdown();


        assertEquals(Money.zero("GBP").amount(), ledger.sumWholeLedger());
        assertEquals(5, succeeded.get());
        assertEquals(5, failed.get());
    }

    @AfterEach
    void cleanup() {
        ledger.deleteAll();
        transactions.deleteAll();
        accountsRepo.findAll().stream()
                .filter(a -> a.getOwnerUserId() != null)   // keep system accounts
                .forEach(accountsRepo::delete);
        users.deleteAll();
    }
}
