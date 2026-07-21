package com.sanim.banking.exception;

public class CurrencyMismatchException extends DomainException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
