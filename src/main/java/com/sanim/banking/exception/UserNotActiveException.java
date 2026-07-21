package com.sanim.banking.exception;

public class UserNotActiveException extends DomainException {
    public UserNotActiveException(String message) {
        super(message);
    }
}
