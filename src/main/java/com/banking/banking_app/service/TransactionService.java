package com.banking.banking_app.service;
import com.banking.banking_app.dto.request.TransferRequest;
import com.banking.banking_app.dto.request.MoneyRequest;
import com.banking.banking_app.dto.response.TransactionResponse;
import com.banking.banking_app.exception.CustomException;
import com.banking.banking_app.model.Account;
import com.banking.banking_app.model.Transaction;
import com.banking.banking_app.model.TransactionType;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(String email, String accountNumber, MoneyRequest req) {
        Account account = loadOwnedAccountForUpdate(email, accountNumber);
        BigDecimal amount = req.getAmount().setScale(2, RoundingMode.HALF_UP);

        account.setBalance(account.getBalance().add(amount));
        return record(account, TransactionType.DEPOSIT, amount, req.getDescription());
    }

    @Transactional
    public TransactionResponse withdraw(String email, String accountNumber, MoneyRequest req) {
        Account account = loadOwnedAccountForUpdate(email, accountNumber);
        BigDecimal amount = req.getAmount().setScale(2, RoundingMode.HALF_UP);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new CustomException("Insufficient funds", HttpStatus.BAD_REQUEST);
        }

        account.setBalance(account.getBalance().subtract(amount));
        return record(account, TransactionType.WITHDRAW, amount, req.getDescription());
    }
    @Transactional
    public TransactionResponse transfer(String email, TransferRequest req) {
        String fromNo = req.getFromAccountNumber();
        String toNo = req.getToAccountNumber();

        if (fromNo.equals(toNo)) {
            throw new CustomException("Cannot transfer to the same account", HttpStatus.BAD_REQUEST);
        }

        BigDecimal amount = req.getAmount().setScale(2, RoundingMode.HALF_UP);

        // ចាក់សោតាមលំដាប់ថេរ ដើម្បីជៀស deadlock
        String firstNo = fromNo.compareTo(toNo) < 0 ? fromNo : toNo;
        String secondNo = fromNo.compareTo(toNo) < 0 ? toNo : fromNo;

        Account first = lockAccount(firstNo);
        Account second = lockAccount(secondNo);

        Account from = first.getAccountNumber().equals(fromNo) ? first : second;
        Account to = (from == first) ? second : first;

        if (!from.getOwner().getEmail().equals(email)) {
            throw new CustomException("You do not own the source account", HttpStatus.FORBIDDEN);
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new CustomException("Insufficient funds", HttpStatus.BAD_REQUEST);
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        String note = req.getDescription();
        TransactionResponse out = record(from, TransactionType.TRANSFER_OUT, amount,
                note != null ? note : "Transfer to " + toNo);
        record(to, TransactionType.TRANSFER_IN, amount,
                note != null ? note : "Transfer from " + fromNo);

        return out;
    }

    private Account lockAccount(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new CustomException(
                        "Account not found: " + accountNumber, HttpStatus.NOT_FOUND));
    }

    // ចាក់សោ row ហើយពិនិត្យថា account ជារបស់ user នេះ
    private Account loadOwnedAccountForUpdate(String email, String accountNumber) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new CustomException("Account not found", HttpStatus.NOT_FOUND));

        if (!account.getOwner().getEmail().equals(email)) {
            throw new CustomException("You do not own this account", HttpStatus.FORBIDDEN);
        }
        return account;
    }

    private TransactionResponse record(Account account, TransactionType type,
                                       BigDecimal amount, String description) {
        accountRepository.save(account);

        Transaction tx = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .description(description)
                .build());

        return TransactionResponse.builder()
                .id(tx.getId())
                .accountNumber(account.getAccountNumber())
                .type(tx.getType())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}