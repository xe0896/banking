package com.sanim.banking.domain;

import java.util.UUID;

// The idea behind this is that it means that this UUID is a TransactionReference and not just
// some random UUID that could be confused with others, the others would have their own class
// to remove that kind of thinking
public record TransactionReference(UUID id) {
    public TransactionReference generate() {
        return new TransactionReference(UUID.randomUUID());
    }
}
