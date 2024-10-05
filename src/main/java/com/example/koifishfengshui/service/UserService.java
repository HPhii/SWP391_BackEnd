package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.request.UpdateUserRequest;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.repository.AccountRepository;
import com.example.koifishfengshui.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getAllUsers(Status status) {
        return userRepository.findByStatus(status);
    }

    // Update
    public UpdateUserRequest updateUser(Long accountId, UpdateUserRequest updateUserDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        User user = account.getUser();

        if (updateUserDTO.getFullName() != null) user.setFullName(updateUserDTO.getFullName());
        if (updateUserDTO.getPhoneNumber() != null) user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        if (updateUserDTO.getBirthdate() != null) user.setBirthdate(updateUserDTO.getBirthdate());
        if (updateUserDTO.getGender() != null) user.setGender(updateUserDTO.getGender());

        userRepository.save(user);

        if (updateUserDTO.getUsername() != null) account.setUsername(updateUserDTO.getUsername());
        if (updateUserDTO.getEmail() != null) account.setEmail(updateUserDTO.getEmail());

        accountRepository.save(account);

        UpdateUserRequest updatedAccount = new UpdateUserRequest();

        updatedAccount.setBirthdate(user.getBirthdate());
        updatedAccount.setUsername(account.getUsername());
        updatedAccount.setGender(user.getGender());
        updatedAccount.setEmail(account.getEmail());
        updatedAccount.setPhoneNumber(user.getPhoneNumber());
        updatedAccount.setFullName(user.getFullName());

        return updatedAccount;
    }


    public User deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Account account = user.getAccount();
        if (account != null) {
            account.setStatus(Status.INACTIVE);
            accountRepository.save(account);
        }
        user.setStatus(Status.INACTIVE);
        return userRepository.save(user);
    }
}
