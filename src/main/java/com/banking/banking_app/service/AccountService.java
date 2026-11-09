package com.banking.banking_app.service;

import com.banking.banking_app.dto.request.OpenAccountRequest;
import com.banking.banking_app.dto.response.AccountResponse;
import com.banking.banking_app.exception.CustomException;
import com.banking.banking_app.model.Account;
import com.banking.banking_app.model.User;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public AccountResponse openAccount(String email, OpenAccountRequest req) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        BigDecimal deposit = req.getInitialDeposit() == null
                ? BigDecimal.ZERO
                : req.getInitialDeposit().setScale(2, RoundingMode.HALF_UP);

        if (deposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException("Initial deposit cannot be negative", HttpStatus.BAD_REQUEST);
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .type(req.getType())
                .balance(deposit)
                .owner(owner)
                .build();

        return toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return accountRepository.findByOwner(owner).stream()
                .map(this::toResponse)
                .toList();
    }

    // បង្កើតលេខ 10 ខ្ទង់ ហើយធានាថាមិនជាន់គ្នា
    private String generateAccountNumber() {
        String number;
        do {
            number = String.valueOf(1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse toResponse(Account a) {
        return AccountResponse.builder()
                .id(a.getId())
                .accountNumber(a.getAccountNumber())
                .type(a.getType())
                .balance(a.getBalance())
                .createdAt(a.getCreatedAt())
                .build();
    }
}