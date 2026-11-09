package com.banking.banking_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class RegisterRequest {
    @NotBlank(message = "Name not Empty")
    private String fullName;

    @Email(message = "Email Correct")
    @NotBlank(message = "Email not Empty")
    private String email;

    @NotBlank(message = "Password not Empty")
    @Size(min = 8, message = "Password at less 8 character")
    private String password;

}
