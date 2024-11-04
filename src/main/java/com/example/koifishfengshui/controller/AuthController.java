package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.config.Filter;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.request.LoginRequest;
import com.example.koifishfengshui.model.request.RegistrationRequest;
import com.example.koifishfengshui.model.request.UpdateFCMRequest;
import com.example.koifishfengshui.model.response.dto.AccountResponse;
import com.example.koifishfengshui.repository.AccountRepository;
import com.example.koifishfengshui.service.AccountService;
import com.example.koifishfengshui.service.AuthenticationService;
import com.example.koifishfengshui.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Filter filter;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationRequest registrationDTO) {
        try {
            AccountResponse newAccount = authenticationService.register(registrationDTO);
            return ResponseEntity.ok(newAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginRequest loginRequestDTO) {
        AccountResponse account = authenticationService.login(loginRequestDTO);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = filter.getToken(request); // Reuse your existing method to extract the token.
        if (token != null) {
            tokenService.invalidateToken(token); // Add logic to blacklist the token.
            return ResponseEntity.ok("Logged out successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request or token missing.");
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> tokenData) {
        String googleToken = tokenData.get("token");
        AccountResponse account = authenticationService.loginGoogle(googleToken);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/fcm")
    public ResponseEntity<?> updateFCM(@RequestBody UpdateFCMRequest updateFCMRequest) {
        Account updatedAccount = accountService.updateFCM(updateFCMRequest);
        return ResponseEntity.ok("FCM token updated successfully");
    }

}
