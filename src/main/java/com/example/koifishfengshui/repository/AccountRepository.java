package com.example.koifishfengshui.repository;


import com.example.koifishfengshui.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByResetPasswordToken(String token);
    public Account findAccountByEmail(String email);
    public Account findAccountById(long id);
}
