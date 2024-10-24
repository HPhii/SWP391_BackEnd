package com.example.koifishfengshui.model.request;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.PondDirection;
import com.example.koifishfengshui.enums.PondShape;
import lombok.Data;

@Data
public class PondFeatureRequest {
    private PondShape shape;
    private String placement;
    private PondDirection direction;
    private String description;
    private FateType compatibleFateType;
}
