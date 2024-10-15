package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.response.dto.TransactionHistoryResponse;
import com.example.koifishfengshui.model.response.paged.PagedTransactionResponse;
import com.example.koifishfengshui.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PagedTransactionResponse> getAllTransactions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedTransactionResponse response = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionById(@PathVariable Long transactionId) {
        TransactionHistoryResponse transactionResponse = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transactionResponse);
    }
}

