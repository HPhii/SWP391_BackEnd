package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.LoginProvider;
import com.example.koifishfengshui.enums.Role;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.exception.DuplicateEntity;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.exception.PasswordMismatchEntity;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.model.request.LoginGoogleRequest;
import com.example.koifishfengshui.model.request.LoginRequest;
import com.example.koifishfengshui.model.request.RegistrationRequest;
import com.example.koifishfengshui.model.response.dto.AccountResponse;
import com.example.koifishfengshui.model.response.dto.EmailDetails;
import com.example.koifishfengshui.repository.AccountRepository;
import com.example.koifishfengshui.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticationManager authenticationManager;

    private static final String WELCOME_SUBJECT = "Welcome to Koi Feng Shui! We're Excited to Have You";
    private static final String WELCOME_TEMPLATE = "welcome-template";

    public AccountResponse register(RegistrationRequest registerRequestDTO) {
        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new PasswordMismatchEntity("Passwords do not match!");
        }

        Account account = modelMapper.map(registerRequestDTO, Account.class);
        try {

            User newUser = new User();
            newUser.setStatus(Status.ACTIVE);
            userRepository.save(newUser);

            account.setUsername(registerRequestDTO.getUsername());
            account.setEmail(registerRequestDTO.getEmail());
            account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            account.setStatus(Status.ACTIVE);
            account.setRole(Role.CUSTOMER);
            account.setLoginProvider(LoginProvider.EMAIL);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setUser(newUser);
            Account newAccount = accountRepository.save(account);

            AccountResponse accountResponse = modelMapper.map(newAccount, AccountResponse.class);
            accountResponse.setUserId(newUser.getUser());

            sendMail(newAccount);

            return accountResponse;
        } catch (Exception e) {
            if (e.getMessage().contains((account.getEmail()))) {
                throw new DuplicateEntity("Duplicate email");
            } else {
                throw new DuplicateEntity("Duplicate entity");
            }
        }

    }

    private void sendMail(Account newAccount) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(newAccount);
        emailDetails.setSubject(WELCOME_SUBJECT);
        Map<String, Object> welcomeContext = Map.of("name", emailDetails.getReceiver().getEmail());
        emailService.sendMail(emailDetails,WELCOME_TEMPLATE , welcomeContext);
    }

    public AccountResponse login(LoginRequest loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getEmail(),
                    loginRequestDTO.getPassword()
            ));

            // => tài khoản có tồn tại
            Account account = (Account) authentication.getPrincipal();
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            accountResponse.setUserId(account.getUser().getUser());
            accountResponse.setToken(tokenService.generateToken(account));
            return accountResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Incorrect Username or Password");
        }
    }

    public AccountResponse loginGoogle(LoginGoogleRequest loginGGRequest) {
        return null;
    }

    public List<Account> getAllAccount() {
        List<Account> listAccount = accountRepository.findAll();
        return listAccount;
    }

    public Account getCurrentAccount() {
        Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findAccountById(currentAccount.getId());
    }

    public AccountResponse getCurrentAccountResponse() {
        Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findAccountById(currentAccount.getId());

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountId(account.getId());
        accountResponse.setEmail(account.getEmail());
        accountResponse.setUserId(account.getUser().getUser());

        return accountResponse;
    }

    public AccountResponse loginGoogleResponse(GoogleIdToken googleIdToken) {
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String email = payload.getEmail();
        Account account = accountRepository.findAccountByEmail(email);
        if (account == null) {
            User newUser = new User();
            newUser.setStatus(Status.ACTIVE);
            newUser.setFullName((String) payload.get("name"));
            userRepository.save(newUser);

            account = new Account();
            account.setEmail(email);
            account.setUsername(((String) payload.get("name")).toLowerCase());
            account.setRole(Role.CUSTOMER);
            account.setLoginProvider(LoginProvider.GOOGLE);
            account.setStatus(Status.ACTIVE);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setUser(newUser);
            accountRepository.save(account);

            sendMail(account);
        }

        String token = tokenService.generateToken(account);
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setEmail(account.getEmail());
        accountResponse.setToken(token);
        return accountResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findAccountByEmail(email);
    }
}
