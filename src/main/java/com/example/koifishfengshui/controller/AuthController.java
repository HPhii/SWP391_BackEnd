package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.response.dto.AccountResponse;
import com.example.koifishfengshui.model.request.LoginRequest;
import com.example.koifishfengshui.model.request.RegistrationRequest;
import com.example.koifishfengshui.service.AccountService;
import com.example.koifishfengshui.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

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

//    @PostMapping("/login-google")
//    private ResponseEntity checkLoginGoogle(@RequestBody LoginGoogleRequest loginGGRequest){
//        AccountResponse accountResponse = authenticationService.loginGoogle(loginGGRequest);
//        return ResponseEntity.ok(accountResponse);
//    }

//    //Log out API
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        if (authentication != null) {
//            new SecurityContextLogoutHandler().logout(request, response, authentication);
//        }
//        return ResponseEntity.ok("Logout successful");
//    }
}
