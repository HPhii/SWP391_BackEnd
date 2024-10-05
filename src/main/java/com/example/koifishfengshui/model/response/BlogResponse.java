package com.example.koifishfengshui.model.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BlogResponse {
    private Long blogId;
    private String title;
    private String thumbnail;
    private String content;
    private String shortDescription;
    private String authorName;
    private String categoryName;
    private String imageUrl;
    private Set<String> tags;
    private Integer viewsCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
