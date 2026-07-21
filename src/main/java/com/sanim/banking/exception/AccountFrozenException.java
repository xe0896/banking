package com.sanim.banking.exception;

public class AccountFrozenException extends DomainException {
    public AccountFrozenException(String message) {
        super(message);
    }
}
