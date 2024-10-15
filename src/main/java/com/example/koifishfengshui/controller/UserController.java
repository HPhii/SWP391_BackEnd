package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.model.request.UpdateUserRequest;
import com.example.koifishfengshui.model.response.dto.UserProfileResponse;
import com.example.koifishfengshui.model.response.paged.PagedUserResponse;
import com.example.koifishfengshui.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class UserController {
    @Autowired
    UserService userService;

    //get current user list
    // api/user/ => GET
    @GetMapping
    public ResponseEntity<PagedUserResponse> getAllActiveUsers(
            @RequestParam Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedUserResponse response = userService.getAllUsers(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

//    @PutMapping("{userId}")
//    public ResponseEntity update(@PathVariable long userId, @Valid @RequestBody User user) {
//        return ResponseEntity.ok(userService.updateUser(userId, user));
//    }

    @PutMapping("/{accountId}")
    public ResponseEntity<UpdateUserRequest> updateUser(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateUserRequest updateUserDTO) {

        UpdateUserRequest updatedUser = userService.updateUser(accountId, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity delete(@PathVariable long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
