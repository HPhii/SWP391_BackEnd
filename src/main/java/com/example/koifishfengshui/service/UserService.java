package com.example.koifishfengshui.service;

import com.example.koifishfengshui.dto.UserRegistrationDTO;
import com.example.koifishfengshui.entity.User;
import com.example.koifishfengshui.enums.Role;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.exception.DuplicateEntity;
import com.example.koifishfengshui.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDTO registerRequestDTO) {
        try {
            User user = new User();
            user.setUsername(registerRequestDTO.getUsername());
            user.setEmail(registerRequestDTO.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            user.setRole(Role.CUSTOMER); // Default role
            user.setStatus(Status.ACTIVE); // Default status
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(user);
        } catch (Exception e) {
            // Log the exception details for debugging
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
