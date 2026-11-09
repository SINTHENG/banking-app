package com.banking.banking_app.controller;

import com.banking.banking_app.dto.request.TransferRequest;
import com.banking.banking_app.dto.response.TransactionResponse;
import com.banking.banking_app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            Authentication auth,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(auth.getName(), request));
    }
}