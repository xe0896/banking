package com.sanim.banking.dto;

import com.sanim.banking.domain.account.AccountType;

import java.util.UUID;

public record AccountResponse (UUID id, String currencyCode, AccountType type) {}

