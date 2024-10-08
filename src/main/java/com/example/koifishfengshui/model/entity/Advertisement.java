package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.enums.ProductType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private FengShuiProduct product;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    @Column(name = "views_count", nullable = false)
    private Integer viewsCount = 0;

    @Column(name = "clicks_count", nullable = false)
    private Integer clicksCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = AdStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
