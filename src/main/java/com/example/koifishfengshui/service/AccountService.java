package com.example.koifishfengshui.service;

import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.request.ForgotPasswordRequest;
import com.example.koifishfengshui.model.response.dto.EmailDetails;
import com.example.koifishfengshui.repository.AccountRepository;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    private static final String RESET_PASSWORD_SUBJECT = "Forgot Your Password?";
    private static final String RESET_PASSWORD_TEMPLATE = "reset-password-template";

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Quên mật khẩu và gửi token qua email
    public void forgotPassword(ForgotPasswordRequest forgotPasswordDTO) throws MessagingException {
        Account account = accountRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found!!!"));

        try {
            String resetToken = UUID.randomUUID().toString();
            account.setResetPasswordToken(resetToken);
            account.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));

            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setReceiver(account);
            emailDetails.setSubject(RESET_PASSWORD_SUBJECT);

            Map<String, Object> resetContext = Map.of(
                    "name", emailDetails.getReceiver().getUsername(),
                    "resetToken", emailDetails.getReceiver().getResetPasswordToken()
            );
            emailService.sendMail(emailDetails, RESET_PASSWORD_TEMPLATE, resetContext);

            accountRepository.save(account);
            logger.info("Reset password email sent to {}", account.getEmail());

        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Internal server error", e);
        }
    }

    public Account getCurrentAccount() {
        Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findAccountById(currentAccount.getId());
    }

    // Đặt lại mật khẩu với token
    public void resetPassword(String token, String newPassword) {
        Optional<Account> optionalAccount = accountRepository.findByResetPasswordToken(token);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("Invalid or expired token");
        }

        Account account = optionalAccount.get();
        if (account.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new EntityNotFoundException("Token has expired");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setResetPasswordToken(null);
        account.setResetPasswordTokenExpiry(null);
        accountRepository.save(account);
        logger.info("Password reset successfully for user: {}", account.getUsername());
    }
}
