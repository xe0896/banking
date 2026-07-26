package com.sanim.banking.dto;

import com.sanim.banking.domain.account.AccountType;
import com.sanim.banking.enums.CurrencyCode;

import java.util.UUID;

public record OpenAccountRequest(UUID userId, AccountType type, CurrencyCode currencyCode) {}
