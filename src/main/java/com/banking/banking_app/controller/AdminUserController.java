package com.banking.banking_app.controller;

import com.banking.banking_app.dto.request.UpdateRoleRequest;
import com.banking.banking_app.dto.response.ApiResponse;
import com.banking.banking_app.dto.response.UserResponse;
import com.banking.banking_app.model.User;
import com.banking.banking_app.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminService adminService;

    // GET /api/admin/users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("បញ្ជី User", adminService.getAllUsers()));
    }

    // PUT /api/admin/users/{id}/role
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal User currentAdmin) {

        UserResponse data =
                adminService.updateRole(id, request.getRole(), currentAdmin);
        return ResponseEntity.ok(
                ApiResponse.success("ប្តូរ Role ជោគជ័យ!", data));
    }
}