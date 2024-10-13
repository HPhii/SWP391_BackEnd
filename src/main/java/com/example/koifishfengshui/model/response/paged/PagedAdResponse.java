package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.response.dto.AdResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedAdResponse {
    private List<AdResponse> ads;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}

