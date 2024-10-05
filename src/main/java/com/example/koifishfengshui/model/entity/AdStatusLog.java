package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.AdStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ad_status_logs")
public class AdStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Advertisement advertisement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "change_time", nullable = false)
    private LocalDateTime changeTime;

    @PrePersist
    protected void onCreate() {
        changeTime = LocalDateTime.now();
    }
}

