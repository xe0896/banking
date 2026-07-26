package com.sanim.banking.domain;
import com.sanim.banking.enums.CurrencyCode;
import com.sanim.banking.exception.CurrencyDigitException;

import java.util.Currency;
import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, CurrencyCode curr) {
    public Money {
        // Validation on what is considered money here, this is the same thing as checking if it is null
        // then throwing a NullPointerException but in one line
        Currency currency = Currency.getInstance(String.valueOf(curr));
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if(currency.getDefaultFractionDigits() != amount.scale()) {
            throw new CurrencyDigitException("Currency digits received: " + amount.scale() + " required: " + currency.getDefaultFractionDigits());
        }
    }

    public Money sum(Money other) {
        if(!curr.equals(other.curr)) throw new IllegalArgumentException("Currency digit does not match");
        return new Money(amount.add(other.amount), curr);
    }

    public boolean isPositive() {
        int res = amount.signum();
        return res == 1;
    }

    public boolean isZero() {
        int res = amount.signum();
        return res == 0;
    }

    public boolean isNegative() {
        int res = amount.signum();
        return res == -1;
    }

    public int compareTo(Money other) {
        if(!curr.equals(other.curr)) throw new IllegalArgumentException("Currency does not match");
        return amount.compareTo(other.amount);
    }

    public Money subtract(Money other) {
        if(!curr.equals(other.curr)) throw new IllegalArgumentException("Currency does not match");
        return new Money(amount.subtract(other.amount), curr);
    }

    public static Money of(String amount, CurrencyCode currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    public static Money zero(CurrencyCode c) {
        Currency currency = Currency.getInstance(String.valueOf(c));
        return new Money(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits()), c);
    }

    public Money negate() {
        return new Money(amount.negate(), curr);
    }

    @Override
    public String toString() {
        return amount.toString() + " " + curr.toString();
    }
}
