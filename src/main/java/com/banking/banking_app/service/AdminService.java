package com.banking.banking_app.service;

import com.banking.banking_app.dto.response.UserResponse;
import com.banking.banking_app.exception.CustomException;
import com.banking.banking_app.model.Role;
import com.banking.banking_app.model.User;
import com.banking.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse updateRole(Long id, Role newRole, User currentAdmin) {

        // 1. រក user គោលដៅ
        User target = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User រកមិនឃើញ!", HttpStatus.NOT_FOUND));

        // 2. ការពារ: ADMIN មិនអាចប្តូរ role ខ្លួនឯង
        if (target.getId().equals(currentAdmin.getId())) {
            throw new CustomException(
                    "មិនអាចប្តូរ Role ខ្លួនឯងបានទេ!", HttpStatus.BAD_REQUEST);
        }

        // 3. ប្តូរ role ហើយ save
        target.setRole(newRole);
        userRepository.save(target);

        return UserResponse.from(target);
    }
}