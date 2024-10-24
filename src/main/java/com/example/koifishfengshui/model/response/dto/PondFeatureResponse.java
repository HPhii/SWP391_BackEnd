package com.example.koifishfengshui.model.response.dto;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.PondDirection;
import com.example.koifishfengshui.enums.PondShape;
import lombok.Data;

@Data
public class PondFeatureResponse {
    private Long pondFeatureId;
    private PondShape shape;
    private String placement;
    private PondDirection direction;
    private FateType compatibleFateType;
    private String description;
}
