package com.banking.banking_app.controller;

import com.banking.banking_app.dto.request.MoneyRequest;
import com.banking.banking_app.dto.response.TransactionResponse;
import com.banking.banking_app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/{accountNumber}")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            Authentication auth,
            @PathVariable String accountNumber,
            @Valid @RequestBody MoneyRequest request) {
        return ResponseEntity.ok(transactionService.deposit(auth.getName(), accountNumber, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            Authentication auth,
            @PathVariable String accountNumber,
            @Valid @RequestBody MoneyRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(auth.getName(), accountNumber, request));
    }
}