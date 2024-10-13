package com.example.koifishfengshui.model.entity;

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

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductType type;

    @ManyToOne
    @JoinColumn(name = "compatible_fate_id")
    private Fate compatibleFate;

    @Column
    private Double price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}

