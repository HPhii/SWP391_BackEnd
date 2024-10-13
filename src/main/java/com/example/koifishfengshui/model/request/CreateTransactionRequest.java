package com.example.koifishfengshui.model.request;

import com.example.koifishfengshui.enums.PaymentMethod;
import lombok.Data;

@Data
public class CreateTransactionRequest {
    private Double amount;
    private PaymentMethod paymentMethod;
}

