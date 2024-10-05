package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.Fate;
import com.example.koifishfengshui.enums.PondDirection;
import com.example.koifishfengshui.enums.PondPlacement;
import com.example.koifishfengshui.enums.PondShape;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "pond_features")
public class PondFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pondFeatureId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PondShape shape;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PondPlacement placement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PondDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "compatible_fate", nullable = false)
    private Fate compatibleFate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "recommendedPond")
    private List<Consultation> consultations;
}
