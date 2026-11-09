package com.banking.banking_app.dto.request;

import com.banking.banking_app.model.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenAccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType type;

    // ប្រាក់ដាក់ដំបូង (មិនបង្ខំ)
    private BigDecimal initialDeposit;
}