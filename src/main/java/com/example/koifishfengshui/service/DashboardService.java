package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.enums.Role;
import com.example.koifishfengshui.model.entity.SubscriptionPlan;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.repository.AccountRepository;
import com.example.koifishfengshui.repository.AdRepository;
import com.example.koifishfengshui.repository.SubscriptionPlanRepository;
import com.example.koifishfengshui.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    AdRepository adRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total Ads in System
        long totalAds = adRepository.count();
        stats.put("totalAds", totalAds);

        // Total Customer
        long totalCustomer = accountRepository.countByRole(Role.CUSTOMER);
        stats.put("totalCustomer", totalCustomer);

        // Popularity of advertising packages
        List<SubscriptionPlan> subscriptionPlans = subscriptionPlanRepository.findAll();
        Map<String, Double> planPopularityPercentage = subscriptionPlans.stream()
                .collect(Collectors.toMap(
                        SubscriptionPlan::getPlanName,
                        plan -> {
                            long adsCountForPlan = plan.getAdvertisements().size();
                            return (totalAds > 0) ? (adsCountForPlan * 100.0 / totalAds) : 0.0;
                        }
                ));
        stats.put("planPopularityPercentage", planPopularityPercentage);

        // Revenue
        double totalRevenue = transactionHistoryRepository.findAll().stream()
                .filter(transaction -> transaction.getPaymentStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(TransactionHistory::getAmount)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        List<User> topSpendingUsers = transactionHistoryRepository.findTopSpendingUsers(PageRequest.of(0, 10));
        stats.put("topSpendingUsers", topSpendingUsers);

        // Transaction History (biểu đồ tròn)
        long successfulTransactions = transactionHistoryRepository.countByPaymentStatus(PaymentStatus.SUCCESS);
        long failedTransactions = transactionHistoryRepository.countByPaymentStatus(PaymentStatus.PENDING);
        stats.put("successfulTransactions", successfulTransactions);
        stats.put("failedTransactions", failedTransactions);

        // Revenue by Month (biểu đồ cột)
        int currentYear = LocalDate.now().getYear();
        List<Object[]> monthlyRevenueData = transactionHistoryRepository.getMonthlyRevenue(currentYear);
        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        for (Object[] row : monthlyRevenueData) {
            Integer month = (Integer) row[0];
            Double revenue = (Double) row[1];
            monthlyRevenue.put(month, revenue);
        }
        stats.put("monthlyRevenue", monthlyRevenue);

        return stats;
    }
}
