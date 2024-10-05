package com.example.koifishfengshui.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class BlogRequest {
    private String title;
    private String thumbnail;
    private String content;
    private String shortDescription;
    private String categoryName;
    private String imageUrl;
    private Set<@Size(max = 50, message = "Tag must not exceed 50 characters") String> tags;
}
