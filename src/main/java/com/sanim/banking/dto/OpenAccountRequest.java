package com.sanim.banking.dto;

import com.sanim.banking.domain.account.AccountType;

import java.util.UUID;

public record OpenAccountRequest(UUID userId, AccountType type, String currencyCode) {}
