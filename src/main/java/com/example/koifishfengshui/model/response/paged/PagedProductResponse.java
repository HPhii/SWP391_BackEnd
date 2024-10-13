package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.entity.FengShuiProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedProductResponse {
    private List<FengShuiProduct> products;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}

