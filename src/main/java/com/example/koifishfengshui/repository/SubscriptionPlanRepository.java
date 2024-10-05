package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    // Find a subscription plan by name
    Optional<SubscriptionPlan> findByPlanName(String planName);

    // Find all subscription plans sorted by price in ascending order
    List<SubscriptionPlan> findAllByOrderByPriceAsc();
}
