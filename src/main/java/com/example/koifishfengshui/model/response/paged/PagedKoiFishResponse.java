package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.response.dto.KoiFishResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedKoiFishResponse {
    private List<KoiFishResponse> koiFish;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
