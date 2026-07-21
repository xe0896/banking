package com.sanim.banking.config;

import com.sanim.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class SystemAccountNumbers {
    public static final String CASH_IN = "00000001";
    public static final String CASH_OUT = "00000002";
    public static final String FEE_REVENUE = "00000003";
    public static final String INTEREST_EXPENSE = "00000004";

    public static UUID getUUIDSystemId(String number, AccountRepository accounts) {
        return accounts.findByAccountNumberValue(number)
                .orElseThrow(() -> new IllegalStateException("system account " + number + " not seeded"))
                .getId();
    }
}
