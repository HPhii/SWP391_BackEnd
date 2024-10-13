package com.example.koifishfengshui.model.request;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.KoiSize;
import lombok.Data;

@Data
public class KoiFishRequest {
    private String species;
    private String color;
    private KoiSize size;
    private double price;
    private String imageUrl;
    private String description;
    private FateType compatibleFateType;
}
