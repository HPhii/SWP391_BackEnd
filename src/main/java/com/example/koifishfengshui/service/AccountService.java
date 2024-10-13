package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.request.ForgotPasswordRequest;
import com.example.koifishfengshui.model.response.dto.AccountResponse;
import com.example.koifishfengshui.model.response.dto.EmailDetails;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.enums.LoginProvider;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.repository.AccountRepository;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Quên mật khẩu và gửi token qua email
    public void forgotPassword(ForgotPasswordRequest forgotPasswordDTO) throws MessagingException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(forgotPasswordDTO.getEmail());
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("Email not found");
        }

        Account account = optionalAccount.get();
        String resetToken = UUID.randomUUID().toString();
        account.setResetPasswordToken(resetToken);
        account.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));  // Token có hạn 30 phút

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(account);
        emailDetails.setSubject(RESET_PASSWORD_SUBJECT);

        Map<String, Object> resetContext = Map.of(
                "name", emailDetails.getReceiver().getUser().getFullName(),
                "resetToken", emailDetails.getReceiver().getResetPasswordToken()
        );
        emailService.sendMail(emailDetails, RESET_PASSWORD_TEMPLATE, resetContext);

        accountRepository.save(account);
        logger.info("Reset password email sent to {}", account.getEmail());
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

    public AccountResponse registerGoogle(String name, String email) {
        Account account = accountRepository.findByEmail(email).orElse(new Account());
        account.setEmail(email);

        // Create Random Password
        if (account.getPassword() == null || account.getPassword().isBlank()) {
            String randomPassword = generateRandomPassword(12);
            account.setPassword(passwordEncoder.encode(randomPassword));
            account.setUsername(name);
            account.setLoginProvider(LoginProvider.GOOGLE);
            account.setStatus(Status.ACTIVE);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());

            // Send Random password to email
            sendPasswordToEmail(email, randomPassword);
        }
        Account newAccount = accountRepository.save(account);
        return modelMapper.map(newAccount, AccountResponse.class);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    private void sendPasswordToEmail(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password for KoiFish Feng Shui");
        message.setText("Dear user,\n\nYour account has been created successfully. Here is your password: " + password +
                "\n\nPlease change your password after logging in.\n\nBest regards,\nKoiFish Feng Shui Team");

        mailSender.send(message);
    }

}
