package com.banking.banking_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email not Correct")
    @NotBlank(message = "Email not Empty")
    private String email;

    @NotBlank(message = "Password not Empty")
    private String password;

}
