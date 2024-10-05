package com.example.koifishfengshui.model.request;

import com.example.koifishfengshui.enums.Fate;
import com.example.koifishfengshui.enums.ProductType;
import lombok.Data;

@Data
public class AdRequest {
    private String productName;
    private ProductType productType;
    private String description;
    private Double price;
    private String imageUrl;
    private String contactInfo;
    private Fate compatibleFate;
}
