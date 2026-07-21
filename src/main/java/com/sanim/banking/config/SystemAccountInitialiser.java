package com.sanim.banking.config;

import com.sanim.banking.domain.account.*;
import com.sanim.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Should be done first before the bootstrap of starting to use repositories, the init of repo's is done before
@RequiredArgsConstructor
public class SystemAccountInitialiser implements CommandLineRunner {

    private final AccountRepository accounts;

    @Override
    public void run(String... args) {
        if (accounts.findByAccountNumberValue(SystemAccountNumbers.CASH_IN).isEmpty()) {
            accounts.save(Account.builder()
                    .accountNumberValue(SystemAccountNumbers.CASH_IN)
                    .type(AccountType.SYSTEM_CASH_IN)
                    .currencyCode("GBP")
                    .status(AccountStatus.OPEN)
                    .build());
            // status and openedAt handled by @PrePersist
        }

        if (accounts.findByAccountNumberValue(SystemAccountNumbers.CASH_OUT).isEmpty()) {
            accounts.save(Account.builder()
                    .accountNumberValue(SystemAccountNumbers.CASH_OUT)
                    .type(AccountType.SYSTEM_CASH_OUT)
                    .currencyCode("GBP")
                    .status(AccountStatus.OPEN)
                    .build());
            // status and openedAt handled by @PrePersist
        }
    }
}