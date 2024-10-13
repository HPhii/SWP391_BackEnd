package com.example.koifishfengshui.model.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long accountId;

    private Long userId;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    private String email;

    private String token;
}
