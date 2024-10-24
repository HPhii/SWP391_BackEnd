package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.KoiSize;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "koi_fish")
public class KoiFish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long koiId;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KoiSize size;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "compatible_fate_id", nullable = false)
    private Fate compatibleFate;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String description;
}


