package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}

