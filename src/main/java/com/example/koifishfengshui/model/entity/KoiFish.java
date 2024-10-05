package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.Fate;
import com.example.koifishfengshui.enums.KoiSize;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fate compatibleFate;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "color_fate")
    private String colorFate;

    @OneToMany(mappedBy = "recommendedKoi")
    private List<Consultation> consultations;
}

