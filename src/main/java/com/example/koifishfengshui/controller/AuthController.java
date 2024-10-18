package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.config.Filter;
import com.example.koifishfengshui.enums.LoginProvider;
import com.example.koifishfengshui.enums.Role;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.response.dto.AccountResponse;
import com.example.koifishfengshui.model.request.LoginRequest;
import com.example.koifishfengshui.model.request.RegistrationRequest;
import com.example.koifishfengshui.repository.AccountRepository;
import com.example.koifishfengshui.service.AccountService;
import com.example.koifishfengshui.service.AuthenticationService;
import com.example.koifishfengshui.service.TokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


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

//    @Value("${GOOGLE_CLIENT_ID}")
//    private String googleClientId;

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

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestParam String idToken) {
        try {
            String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                AccountResponse accountResponse = authenticationService.loginGoogleResponse(googleIdToken);

                return ResponseEntity.ok(accountResponse);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google login failed.");
        }
    }



}
