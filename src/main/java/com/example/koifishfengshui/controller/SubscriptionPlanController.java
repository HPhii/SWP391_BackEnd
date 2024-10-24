package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.entity.SubscriptionPlan;
import com.example.koifishfengshui.service.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    // Get all subscription plans
    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptionPlans() {
        return ResponseEntity.ok(subscriptionPlanService.getAllSubscriptionPlans());
    }

    // Get a subscription plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getSubscriptionPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionPlanService.getSubscriptionPlanById(id));
    }

    // Create a new subscription plan
    @PostMapping
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(@RequestBody SubscriptionPlan subscriptionPlan) {
        return ResponseEntity.ok(subscriptionPlanService.createSubscriptionPlan(subscriptionPlan));
    }

    // Update an existing subscription plan
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(
            @PathVariable Long id, @RequestBody SubscriptionPlan subscriptionPlan) {
        return ResponseEntity.ok(subscriptionPlanService.updateSubscriptionPlan(id, subscriptionPlan));
    }

    // Delete a subscription plan
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.noContent().build();
    }
}

