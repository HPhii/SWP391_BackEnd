package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.Fate;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consultationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fate fate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "recommended_koi_id", nullable = false)
    private KoiFish recommendedKoi;

    @ManyToOne
    @JoinColumn(name = "feng_shui_content_id", nullable = false)
    private FengShuiContent fengShuiContent;

    @ManyToOne
    @JoinColumn(name = "recommended_pond_id", nullable = false)
    private PondFeature recommendedPond;

    @Column(name = "consultation_date", nullable = false)
    private LocalDateTime consultationDate;

    @Column(name = "compatibility_rate", nullable = false)
    private Integer compatibilityRate;

    @PrePersist
    protected void onCreate() {
        consultationDate = LocalDateTime.now();
    }
}
