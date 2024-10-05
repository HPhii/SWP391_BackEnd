package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.request.UpdateUserRequest;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.enums.Status;
import com.example.koifishfengshui.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity getAllActiveUsers(@RequestParam Status status) {
        List<User> users = userService.getAllUsers(status);
        return ResponseEntity.ok(users);
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
