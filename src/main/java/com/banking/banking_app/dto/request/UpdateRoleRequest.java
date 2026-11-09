package com.banking.banking_app.dto.request;

import com.banking.banking_app.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class UpdateRoleRequest {
    @NotNull(message = "Role not Empty")
    private Role role;
}
