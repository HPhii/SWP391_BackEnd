package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.SubscriptionPriority;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @OneToMany(mappedBy = "subscriptionPlan")
    private List<Advertisement> advertisements;

    @Column(name = "plan_name", nullable = false, unique = true)
    private String planName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "max_ads", nullable = false)
    private Integer maxAds;

    @Enumerated(EnumType.STRING)
    @Column(name = "ad_placement_priority", nullable = false)
    private SubscriptionPriority adPlacementPriority;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
