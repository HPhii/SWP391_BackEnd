package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.entity.SubscriptionPlan;
import com.example.koifishfengshui.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanService {

    @Autowired
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAllByOrderByPriceAsc();
    }

    public SubscriptionPlan getSubscriptionPlanById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + id));
    }

    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        if (subscriptionPlanRepository.findByPlanName(subscriptionPlan.getPlanName()).isPresent()) {
            throw new RuntimeException("Subscription Plan with the name '" + subscriptionPlan.getPlanName() + "' already exists");
        }
        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    public SubscriptionPlan updateSubscriptionPlan(Long id, SubscriptionPlan updatedSubscriptionPlan) {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + id));

        existingPlan.setPlanName(updatedSubscriptionPlan.getPlanName());
        existingPlan.setPrice(updatedSubscriptionPlan.getPrice());
        existingPlan.setDuration(updatedSubscriptionPlan.getDuration());
        existingPlan.setAdPlacementPriority(updatedSubscriptionPlan.getAdPlacementPriority());
        existingPlan.setDescription(updatedSubscriptionPlan.getDescription());

        return subscriptionPlanRepository.save(existingPlan);
    }

    public void deleteSubscriptionPlan(Long id) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + id));

        subscriptionPlanRepository.delete(subscriptionPlan);
    }
}
