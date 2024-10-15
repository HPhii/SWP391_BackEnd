package com.example.koifishfengshui.model.response.paged;

import com.example.koifishfengshui.model.response.dto.TransactionHistoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedTransactionResponse {
    private List<TransactionHistoryResponse> transactions;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}