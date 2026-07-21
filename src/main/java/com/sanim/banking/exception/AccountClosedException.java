package com.sanim.banking.exception;

public class AccountClosedException extends DomainException {
    public AccountClosedException(String message) {
        super(message);
    }
}
