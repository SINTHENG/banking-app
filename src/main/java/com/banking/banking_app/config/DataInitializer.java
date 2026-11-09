package com.banking.banking_app.config;

import com.banking.banking_app.model.Role;
import com.banking.banking_app.model.User;
import com.banking.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfNotExists("System Admin", "admin@bank.com", "Admin@12345", Role.ADMIN);
        createUserIfNotExists("Bank Teller", "teller@bank.com", "Teller@12345", Role.TELLER);
    }

    private void createUserIfNotExists(String name, String email,
                                       String rawPassword, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .build());
        }
    }
}