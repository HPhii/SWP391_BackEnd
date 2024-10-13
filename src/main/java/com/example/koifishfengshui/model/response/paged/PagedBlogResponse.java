package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.response.dto.BlogResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedBlogResponse {
    private List<BlogResponse> blogs;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}

