package com.example.koifishfengshui.model.entity;

import com.example.koifishfengshui.enums.Fate;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "feng_shui_contents")
public class FengShuiContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Fate fate;  // Mệnh của người dùng

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;  // Mô tả về phong thủy liên quan đến mệnh

//    @OneToMany(mappedBy = "fengShuiContent")
//    @JsonIgnore
//    private List<Consultation> consultations;
}
