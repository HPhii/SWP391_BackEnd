package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.response.dto.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedTransactionHistoryResponse {
    private List<TransactionResponse> transactions;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
