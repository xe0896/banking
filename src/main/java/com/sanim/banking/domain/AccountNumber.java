package com.sanim.banking.domain;

import java.util.concurrent.ThreadLocalRandom;

public record AccountNumber(String accountNumber) {
    public AccountNumber {
        if(accountNumber.length() != 8) {
            throw new IllegalArgumentException("Account number must be 8 digits long");
        }
    }

    public static AccountNumber generate() {
        ThreadLocalRandom generator = ThreadLocalRandom.current();
        // A number between 10_000_000 and 99_999_999
        return new AccountNumber(Integer.toString(generator.nextInt(10_000_000, 100_000_000)));
    }
}
