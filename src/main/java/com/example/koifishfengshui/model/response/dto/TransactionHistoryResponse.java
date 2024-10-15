package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.enums.PaymentMethod;
import com.example.koifishfengshui.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionHistoryResponse {
    private Long transactionId;
    private String userName;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime transactionDate;
}

