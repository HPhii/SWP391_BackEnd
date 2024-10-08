package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.PondDirection;
import com.example.koifishfengshui.enums.PondShape;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false)
    private String placement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PondDirection direction;

    @ManyToOne
    @JoinColumn(name = "compatible_fate_id", nullable = false)
    private Fate compatibleFate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}

