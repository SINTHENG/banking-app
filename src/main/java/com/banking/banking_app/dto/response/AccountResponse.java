package com.banking.banking_app.dto.response;

import com.banking.banking_app.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private AccountType type;
    private BigDecimal balance;
    private Instant createdAt;
}