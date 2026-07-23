package com.sanim.banking.config;

import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.user.User;
import com.sanim.banking.repository.UserRepository;
import com.sanim.banking.service.AccountService;
import com.sanim.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Currency;


@Component
@Profile("playground") // only runs when this profile is active
@Order(10) // after the seeder
@RequiredArgsConstructor
public class Playground implements CommandLineRunner {
    private final UserRepository users;
    private final AccountService accounts;
    private final TransactionService transactions;

    @Override
    public void run(String... args) {
        var user = users.save(User.builder()
                .email("alice@example.com")
                .passwordHash("hash")
                .displayName("Alice")
                .build());

        System.out.println("UserID: " + user.getId());

        var acc = accounts.openAccount(user.getId(),
                AccountType.CHECKING, "GBP");

        transactions.deposit(acc.getId(), Money.of("100.00", "GBP"), user.getId(), "play-1");
        transactions.deposit(acc.getId(), Money.of("100.00", "GBP"), user.getId(), "idom-1");
        transactions.deposit(acc.getId(), Money.of("200.00", "GBP"), user.getId(), "idom-2");

        System.out.println("balance: " + accounts.getBalance(acc.getId()));
    }
}