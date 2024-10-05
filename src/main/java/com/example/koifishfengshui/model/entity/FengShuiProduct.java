package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.Fate;
import com.example.koifishfengshui.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "feng_shui_products")
public class FengShuiProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Advertisement> advertisements;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "compatible_fate", nullable = false)
    private Fate compatibleFate;

    @Column(nullable = false)
    private Double price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
