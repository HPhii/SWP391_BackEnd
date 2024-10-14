package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.PaymentMethod;
import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.model.response.paged.PagedTransactionResponse;
import com.example.koifishfengshui.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Transactional
    public TransactionHistory createTransaction(User user, Advertisement advertisement, Double amount) {
        TransactionHistory transaction = new TransactionHistory();
        transaction.setUser(user);
        transaction.setAdvertisement(advertisement);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(PaymentMethod.VNPAY);
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        return transactionHistoryRepository.save(transaction);
    }

    @Transactional
    public PagedTransactionResponse getAllTransactions(Pageable pageable) {
        Page<TransactionHistory> transactionPage = transactionHistoryRepository.findAll(pageable);
        List<TransactionHistory> transactions = transactionPage.getContent();

        return new PagedTransactionResponse(
                transactions,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }
}

