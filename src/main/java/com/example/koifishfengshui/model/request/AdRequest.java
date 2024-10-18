package com.example.koifishfengshui.model.request;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.ProductType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AdRequest {
    private String productName;
    private ProductType productType;
    private String description;
    private Double price;
    private MultipartFile imageFile;
    private String contactInfo;
    private FateType compatibleFate;
}
