package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.response.AccountResponse;
import com.example.koifishfengshui.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2LoginController {

    @Autowired
    private AccountService accountService;


    //    @PostMapping("/loginSuccess")
//    @ResponseBody
//    public ResponseEntity<String> loginWithGoogle(OAuth2AuthenticationToken authentication) {
//        try {
//            OAuth2User oAuth2User = authentication.getPrincipal();
//
//            // Extract attributes
//            String name = oAuth2User.getAttribute("name");
//            String email = oAuth2User.getAttribute("email");
//            String picture = oAuth2User.getAttribute("picture");
//
//            // Validate email presence
//            if (email == null || email.isBlank()) {
//                return ResponseEntity.badRequest().body("Login failed: Email not found.");
//            }
//
//            // Register or update user
//            userService.registerGoogle(name, email, picture);
//
//            return ResponseEntity.ok("Login success!");
//
//        } catch (Exception e) {
//            // Log exception (you can use a proper logging framework like SLF4J)
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("An error occurred during login: " + e.getMessage());
//        }
//    }
    @GetMapping("/loginSuccess")
    public ResponseEntity loginSuccess(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        AccountResponse account = accountService.registerGoogle(name, email);
        return ResponseEntity.ok(account);
    }

}
