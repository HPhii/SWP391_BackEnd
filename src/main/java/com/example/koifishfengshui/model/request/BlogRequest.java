package com.example.koifishfengshui.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class BlogRequest {
    private String title;
    private MultipartFile thumbnail;
    private String content;
    private String shortDescription;
    private String categoryName;
    private MultipartFile imageFile;
    private Set<@Size(max = 50, message = "Tag must not exceed 50 characters") String> tags;
}
