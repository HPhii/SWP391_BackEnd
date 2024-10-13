package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.KoiSize;
import lombok.Data;

@Data
public class KoiFishResponse {
    private Long koiId;
    private String species;
    private String color;
    private KoiSize size;
    private double price;
    private FateType compatibleFateType;
    private String imageUrl;
    private String description;
}
