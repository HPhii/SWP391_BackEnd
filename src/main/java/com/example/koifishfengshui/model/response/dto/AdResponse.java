package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdResponse {
    private Long adId;
    private String productName;
    private ProductType productType;
    private String description;
    private Double price;
    private String imageUrl;
    private String contactInfo;
    private AdStatus status;
    private Integer viewsCount;
    private Integer clicksCount;
    private LocalDateTime createdAt;
    private String userName;
}

