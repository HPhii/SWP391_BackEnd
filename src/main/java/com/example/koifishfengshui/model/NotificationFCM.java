package com.example.koifishfengshui.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationFCM {
    String title;
    String message;
    String fcmToken;
}
