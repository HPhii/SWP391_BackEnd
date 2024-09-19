package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.dto.UserRegistrationDTO;
import com.example.koifishfengshui.dto.LoginRequestDTO;
import com.example.koifishfengshui.entity.User;
import com.example.koifishfengshui.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            User registeredUser = userService.registerUser(userRegistrationDTO);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<User> userOptional = userService.findByEmail(loginRequestDTO.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userService.checkPassword(loginRequestDTO.getPassword(), user.getPassword())) {
                return ResponseEntity.ok("Login successful!");
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
