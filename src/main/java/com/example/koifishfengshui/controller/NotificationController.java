package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.NotificationFCM;
import com.example.koifishfengshui.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/notification")
@SecurityRequirement(name = "api")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping
    public void sendNotification(@RequestBody NotificationFCM notificationFCM) {
        notificationService.sendNotification(notificationFCM);
    }
}
