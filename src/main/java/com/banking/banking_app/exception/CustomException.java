package com.banking.banking_app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter               // ✅ Getter ប៉ុណ្ណោះ (មិនប្រើ @Data)
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus status;
}