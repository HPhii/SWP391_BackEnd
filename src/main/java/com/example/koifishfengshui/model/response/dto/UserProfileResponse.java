package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthdate;
    private Gender gender;
    private String email;
}