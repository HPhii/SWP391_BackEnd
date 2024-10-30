package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import com.example.koifishfengshui.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    // Đếm số lượng giao dịch theo trạng thái thanh toán
    long countByPaymentStatus(PaymentStatus paymentStatus);

    // Tính tổng doanh thu từ các giao dịch thành công
    @Query("SELECT SUM(t.amount) FROM TransactionHistory t WHERE t.paymentStatus = 'SUCCESS'")
    Double getTotalRevenue();

    // Lấy danh sách top 10 người dùng chi tiêu nhiều nhất
    @Query("SELECT t.user FROM TransactionHistory t WHERE t.paymentStatus = 'SUCCESS' GROUP BY t.user ORDER BY SUM(t.amount) DESC")
    List<User> findTopSpendingUsers(Pageable pageable);

    // Truy vấn tổng doanh thu theo từng tháng trong năm hiện tại
    @Query("SELECT MONTH(t.transactionDate) AS month, SUM(t.amount) AS revenue " +
            "FROM TransactionHistory t " +
            "WHERE t.paymentStatus = 'SUCCESS' AND YEAR(t.transactionDate) = :year " +
            "GROUP BY MONTH(t.transactionDate)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);
}

