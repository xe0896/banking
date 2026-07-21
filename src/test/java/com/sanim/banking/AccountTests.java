package com.sanim.banking;

import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.account.Account;
import com.sanim.banking.domain.account.AccountStatus;
import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.AccountRepository;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.AccountService;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AccountTests {
    // The idea behind this is that AccountService have private fields, so we cannot access the user repository
    // which we need to first insert the user that is why there seems to be a duplicated instance and there isn't
    // also since Spring ensures only one instance of UserRepository exists so even if AccountService
    // creates a seemingly new one it doesn't cause of singleton scope from the interface extending JpaRepository
    @Autowired
    AccountService accounts;
    @Autowired
    UserRepository users;

    @Test
    void openAccountTest() {
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        UUID userId = u.getId();

        Account account = accounts.openAccount(userId, AccountType.SAVINGS, "GBP");

        boolean res = (account.getAccountNumberValue() != null) && (account.getStatus() == AccountStatus.OPEN);
        assertTrue(res);
    }

    @Test
    void emptyAccountBalance() {
        User u = User.builder().email("fake-email").displayName("display-name").passwordHash("password-hash").build();
        users.save(u);

        UUID userId = u.getId();

        Account account = accounts.openAccount(userId, AccountType.SAVINGS, "GBP");
        String currencyCode = account.getCurrencyCode();

        Money money = accounts.getBalance(account.getId());

        System.out.println(money);
        System.out.println(Money.zero(currencyCode));

        System.out.println("expected scale: " + money.amount().scale());
        System.out.println("actual scale: " + Money.zero(currencyCode).amount().scale());

        assertEquals(Money.zero(currencyCode), money);
    }
}
