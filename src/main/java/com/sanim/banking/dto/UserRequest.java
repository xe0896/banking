package com.sanim.banking.dto;

public record UserRequest(String email, String displayName, String passwordHash) {}
