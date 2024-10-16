package com.example.koifishfengshui.service;

import com.example.koifishfengshui.exception.AuthException;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final String SECRET_KEY = "hieuphinehehe070520030937874259hieuphinehehe";
    private final Map<String, LocalDateTime> tokenBlacklist = new ConcurrentHashMap<>();

    @Autowired
    private AccountRepository accountRepository;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Create Token
    public String generateToken(Account account) {
        String token = Jwts.builder()
                .subject(account.getId()+"")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignKey())
                .compact();
        return token;
    }

    //Verify Token
    public Account getAccountByToken(String token) {
        if (isTokenBlacklisted(token)) {
            throw new AuthException("Token has been invalidated!");
        }

        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String idString = claims.getSubject();
        long id = Long.parseLong(idString);

        return accountRepository.findAccountById(id);
    }

    public void invalidateToken(String token) {
        // Blacklist the token with its expiry time.
        tokenBlacklist.put(token, LocalDateTime.now().plusHours(1)); // Optional: token lifespan.
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }
}

