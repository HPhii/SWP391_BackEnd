package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.KoiSize;
import com.example.koifishfengshui.enums.PondDirection;
import com.example.koifishfengshui.enums.PondShape;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.model.entity.KoiFish;
import com.example.koifishfengshui.model.entity.PondFeature;
import com.example.koifishfengshui.repository.FateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CompatibilityService {

    @Autowired
    private final FateRepository fateRepository;

    private static final Map<FateType, Set<PondDirection>> directionCompatibilityMap = Map.of(
            FateType.METAL, Set.of(PondDirection.NORTHWEST, PondDirection.NORTH, PondDirection.SOUTHEAST),
            FateType.WOOD, Set.of(PondDirection.NORTH, PondDirection.EAST, PondDirection.SOUTH, PondDirection.SOUTHEAST),
            FateType.WATER, Set.of(PondDirection.NORTH, PondDirection.SOUTHEAST),
            FateType.FIRE, Set.of(PondDirection.SOUTH, PondDirection.SOUTHWEST, PondDirection.NORTHEAST),
            FateType.EARTH, Set.of(PondDirection.SOUTHWEST, PondDirection.NORTHEAST)
    );

    public CompatibilityService(FateRepository fateRepository) {
        this.fateRepository = fateRepository;
    }

    public double calculateKoiCompatibility(FateType userFateType, KoiFish koi) {
        Fate userFate = fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new EntityNotFoundException("Fate not found!"));
        Fate koiFate = koi.getCompatibleFate();

        double compatibilityRate = 0;

        List<String> koiColors = Arrays.asList(koi.getColor().split(",\\s*"));

        if (userFate.getFateType() == koiFate.getFateType()) {
            compatibilityRate += 0.8;
        }

        for (String koiColor : koiColors) {
            if (userFate.getCompatibleColors().contains(koiColor)) {
                compatibilityRate += 0.2;
                break;
            } else if (userFate.getIncompatibleColors().contains(koiColor)) {
                compatibilityRate -= 0.1;
                break;
            }
        }

        if (isSizeCompatible(userFate.getFateType(), koi.getSize())) {
            compatibilityRate += 0.1;
        }

        if (userFate.getIncompatibleFates().contains(koiFate)) {
            compatibilityRate -= 0.2;
        }

        return Math.max(0, Math.min(compatibilityRate, 1));
    }


    public double calculatePondCompatibility(FateType userFateType, PondFeature pond) {
        Fate userFate = fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new EntityNotFoundException("Fate not found!"));
        Fate pondFate = pond.getCompatibleFate();

        double compatibilityRate = 0;

        if (userFate.getFateType() == pondFate.getFateType()) {
            compatibilityRate += 0.8;
        }

        if (isDirectionCompatible(userFate.getFateType(), pond.getDirection())) {
            compatibilityRate += 0.1;
        }

        if (isShapeCompatible(userFate.getFateType(), pond.getShape())) {
            compatibilityRate += 0.1;
        }

        if (userFate.getIncompatibleFates().contains(pondFate)) {
            compatibilityRate -= 0.2;
        }

        return Math.max(0, Math.min(compatibilityRate, 1));
    }

//    private double calculateBaseCompatibility(Fate userFate, Fate targetFate, double sameFateBonus) {
//        double compatibilityRate = 0;
//
//        if (userFate.getFateType() == targetFate.getFateType()) {
//            compatibilityRate += sameFateBonus;
//        }
//
//        if (userFate.getIncompatibleFates().contains(targetFate)) {
//            compatibilityRate -= 0.2;
//        }
//
//        return compatibilityRate;
//    }
//
//    public double calculateKoiCompatibility(FateType userFateType, KoiFish koi) {
//        Fate userFate = fateRepository.findByFateType(userFateType)
//                .orElseThrow(() -> new EntityNotFoundException("Fate not found!"));
//
//        double compatibilityRate = calculateBaseCompatibility(userFate, koi.getCompatibleFate(), 0.6);
//
//        if (userFate.getCompatibleColors().contains(koi.getColor())) {
//            compatibilityRate += 0.2;
//        } else if (userFate.getIncompatibleColors().contains(koi.getColor())) {
//            compatibilityRate -= 0.1;
//        }
//
//        if (isSizeCompatible(userFate.getFateType(), koi.getSize())) {
//            compatibilityRate += 0.1;
//        }
//
//        return Math.max(0, Math.min(compatibilityRate, 1));
//    }
//
//    public double calculatePondCompatibility(FateType userFateType, PondFeature pond) {
//        Fate userFate = fateRepository.findByFateType(userFateType)
//                .orElseThrow(() -> new EntityNotFoundException("Fate not found!"));
//
//        double compatibilityRate = calculateBaseCompatibility(userFate, pond.getCompatibleFate(), 0.6);
//
//        if (isDirectionCompatible(userFate.getFateType(), pond.getDirection())) {
//            compatibilityRate += 0.1;
//        }
//
//        if (isShapeCompatible(userFate.getFateType(), pond.getShape())) {
//            compatibilityRate += 0.1;
//        }
//
//        return Math.max(0, Math.min(compatibilityRate, 1));
//    }

    public double calculateProductCompatibility(FateType userFate, FengShuiProduct product) {
        double compatibilityRate = 0;

        if (userFate == product.getCompatibleFate().getFateType()) {
            compatibilityRate += 1;
        }

        return Math.max(0, Math.min(compatibilityRate, 1));
    }

    private boolean isSizeCompatible(FateType userFate, KoiSize size) {
        switch (userFate) {
            case METAL:
                return size == KoiSize.LARGE;
            case WOOD:
                return size == KoiSize.MEDIUM;
            case WATER:
                return size == KoiSize.SMALL;
            case FIRE:
                return size == KoiSize.LARGE;
            case EARTH:
                return size == KoiSize.MEDIUM;
            default:
                return false;
        }
    }

    private boolean isDirectionCompatible(FateType userFate, PondDirection direction) {
        switch (userFate) {
            case METAL:
                return direction == PondDirection.NORTHWEST || direction == PondDirection.NORTH || direction == PondDirection.SOUTHEAST;
            case WOOD:
                return direction == PondDirection.NORTH || direction == PondDirection.EAST || direction == PondDirection.SOUTH || direction == PondDirection.SOUTHEAST;
            case WATER:
                return direction == PondDirection.NORTH || direction == PondDirection.SOUTHEAST;
            case FIRE:
                return direction == PondDirection.SOUTH || direction == PondDirection.SOUTHWEST || direction == PondDirection.NORTHEAST;
            case EARTH:
                return direction == PondDirection.SOUTHWEST || direction == PondDirection.NORTHEAST;
            default:
                return false;
        }
    }

//    private boolean isDirectionCompatible(FateType userFate, PondDirection direction) {
//        return directionCompatibilityMap.getOrDefault(userFate, Set.of()).contains(direction);
//    }

    private boolean isShapeCompatible(FateType userFate, PondShape shape) {
        switch (userFate) {
            case METAL:
                return shape == PondShape.CIRCULAR;
            case WOOD:
                return shape == PondShape.RECTANGULAR;
            case WATER:
                return shape == PondShape.FREE_FORM;
            case FIRE:
                return shape == PondShape.TRIANGULAR;
            case EARTH:
                return shape == PondShape.SQUARE;
            default:
                return false;
        }
    }
}






