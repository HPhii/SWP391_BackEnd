package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.PaymentMethod;
import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.*;
import com.example.koifishfengshui.model.response.dto.TransactionHistoryResponse;
import com.example.koifishfengshui.model.response.dto.TransactionResponse;
import com.example.koifishfengshui.model.response.paged.PagedTransactionHistoryResponse;
import com.example.koifishfengshui.model.response.paged.PagedTransactionResponse;
import com.example.koifishfengshui.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public TransactionHistory createTransaction(User user, Advertisement advertisement, Double amount, SubscriptionPlan plan) {
        TransactionHistory transaction = new TransactionHistory();
        transaction.setUser(user);
        transaction.setAdvertisement(advertisement);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(PaymentMethod.VNPAY);
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        transaction.setSubscriptionPlan(plan);
        return transactionHistoryRepository.save(transaction);
    }

    @Transactional
    public PagedTransactionResponse getAllTransactions(Pageable pageable) {
        Page<TransactionHistory> transactionPage = transactionHistoryRepository.findAll(pageable);
        List<TransactionHistoryResponse> transactionResponses = transactionPage.getContent().stream()
                .map(transaction -> new TransactionHistoryResponse(
                        transaction.getTransactionId(),
                        transaction.getUser().getFullName(),
                        transaction.getAmount(),
                        transaction.getPaymentMethod(),
                        transaction.getPaymentStatus(),
                        transaction.getTransactionDate()
                )).collect(Collectors.toList());

        return new PagedTransactionResponse(
                transactionResponses,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }

    @Transactional
    public TransactionHistoryResponse getTransactionById(Long transactionId) {
        TransactionHistory transaction = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        return new TransactionHistoryResponse(
                transaction.getTransactionId(),
                transaction.getUser().getFullName(),
                transaction.getAmount(),
                transaction.getPaymentMethod(),
                transaction.getPaymentStatus(),
                transaction.getTransactionDate()
        );
    }

    @Transactional
    public PagedTransactionHistoryResponse getTransactionsByUser(Pageable pageable) {
        Account account = authenticationService.getCurrentAccount();
        Long userId = account.getUser().getUser();
        Page<TransactionHistory> transactionPage = transactionHistoryRepository.findByUserUser(userId, pageable);
        List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getTransactionId(),
                        transaction.getUser().getFullName(),
                        transaction.getAmount(),
                        transaction.getPaymentMethod(),
                        transaction.getPaymentStatus(),
                        transaction.getTransactionDate(),
                        transaction.getSubscriptionPlan().getPlanName()
                )).collect(Collectors.toList());

        return new PagedTransactionHistoryResponse(
                transactionResponses,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }
}

