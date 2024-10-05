package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.request.ForgotPasswordRequest;
import com.example.koifishfengshui.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordController {
    @Autowired
    private AccountService accountService;

    // Forgot Password
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordDTO) {
        try {
            accountService.forgotPassword(forgotPasswordDTO);
            return ResponseEntity.ok("Password reset email sent!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Reset Password
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            accountService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
