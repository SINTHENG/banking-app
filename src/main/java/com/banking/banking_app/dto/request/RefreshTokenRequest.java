package com.banking.banking_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token មិនអាចទទេ")
    private String refreshToken;
}
