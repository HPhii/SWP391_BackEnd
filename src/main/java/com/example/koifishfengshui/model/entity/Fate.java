package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.FateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "fate")
public class Fate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private FateType fateType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "compatible_colors", joinColumns = @JoinColumn(name = "fate_id"))
    @Column(name = "color")
    private List<String> compatibleColors;

    @ElementCollection
    @CollectionTable(name = "incompatible_colors", joinColumns = @JoinColumn(name = "fate_id"))
    @Column(name = "color")
    private List<String> incompatibleColors;

    @ManyToMany
    @JoinTable(
            name = "fate_compatibilities",
            joinColumns = @JoinColumn(name = "fate_id"),
            inverseJoinColumns = @JoinColumn(name = "compatible_fate_id")
    )
    @JsonIgnore
    private List<Fate> compatibleFates;

    @ManyToMany
    @JoinTable(
            name = "fate_incompatibilities",
            joinColumns = @JoinColumn(name = "fate_id"),
            inverseJoinColumns = @JoinColumn(name = "incompatible_fate_id")
    )
    @JsonIgnore
    private List<Fate> incompatibleFates;

}

