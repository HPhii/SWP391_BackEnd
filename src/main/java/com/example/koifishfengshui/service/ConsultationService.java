package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.KoiSize;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.model.entity.KoiFish;
import com.example.koifishfengshui.model.entity.PondFeature;
import com.example.koifishfengshui.repository.FateRepository;
import com.example.koifishfengshui.repository.FengShuiProductRepository;
import com.example.koifishfengshui.repository.KoiFishRepository;
import com.example.koifishfengshui.repository.PondFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private PondFeatureRepository pondFeatureRepository;

    @Autowired
    private FengShuiProductRepository fengShuiProductRepository;

    @Autowired
    private FateRepository fateRepository;

    @Autowired
    private CompatibilityService compatibilityService;

    public Fate getUserFate(FateType userFateType) {
        return fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new RuntimeException("Fate not found for type: " + userFateType));
    }

    public List<Map<String, Object>> getKoiRecommendations(FateType userFateType) {
        Fate userFate = getUserFate(userFateType);

        List<KoiFish> koiRecommendations = koiFishRepository.findByCompatibleFate(userFate);

        KoiSize recommendedSize = getRecommendedSize(userFateType);

        return koiRecommendations.stream()
                .map(koi -> {
                    double compatibilityRate = compatibilityService.calculateKoiCompatibility(userFateType, koi);
                    if (compatibilityRate >= 0.5) {
                        Map<String, Object> koiResult = new HashMap<>();
                        koiResult.put("koi", Map.of(
                                "koiId", koi.getKoiId(),
                                "species", koi.getSpecies(),
                                "color", koi.getColor(),
                                "description", koi.getDescription(),
                                "marketValue", koi.getPrice(),
                                "imageUrl", koi.getImageUrl()
                        ));
                        koiResult.put("compatibilityRate", (compatibilityRate * 100.0));
                        koiResult.put("compatibleFate", koi.getCompatibleFate().getFateType());
                        koiResult.put("recommendSize", recommendedSize);
                        return koiResult;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream().limit(3).collect(Collectors.toList());
                }));
    }

    public List<Map<String, Object>> getPondRecommendations(FateType userFateType) {
        Fate userFate = getUserFate(userFateType);

        List<PondFeature> pondRecommendations = pondFeatureRepository.findByCompatibleFate(userFate);

        return pondRecommendations.stream()
                .map(pond -> {
                    double compatibilityRate = compatibilityService.calculatePondCompatibility(userFateType, pond);
                    if (compatibilityRate >= 0.5) {
                        Map<String, Object> pondResult = new HashMap<>();
                        pondResult.put("pond", Map.of(
                                "pondFeatureId", pond.getPondFeatureId(),
                                "shape", pond.getShape(),
                                "placement", pond.getPlacement(),
                                "direction", pond.getDirection(),
                                "description", pond.getDescription()
                        ));
                        pondResult.put("compatibilityRate", (compatibilityRate * 100.0));
                        pondResult.put("compatibleFate", pond.getCompatibleFate().getFateType());
                        return pondResult;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream().limit(3).collect(Collectors.toList());
                }));
    }

    public List<Map<String, Object>> getFengShuiProductRecommendations(FateType userFateType) {
        Fate userFate = getUserFate(userFateType);

        List<FengShuiProduct> productRecommendations = fengShuiProductRepository.findByCompatibleFate(userFate);

        return productRecommendations.stream()
                .map(product -> {
                    double compatibilityRate = compatibilityService.calculateProductCompatibility(userFateType, product);
                    if (compatibilityRate >= 0.5) {
                        Map<String, Object> productResult = new HashMap<>();
                        productResult.put("product", Map.of(
                                "productId", product.getProductId(),
                                "name", product.getName(),
                                "type", product.getType(),
                                "price", product.getPrice(),
                                "imageUrl", product.getImageUrl(),
                                "description", product.getDescription()
                        ));
                        productResult.put("compatibilityRate", (compatibilityRate * 100.0));
                        productResult.put("compatibleFate", product.getCompatibleFate().getFateType());
                        return productResult;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream().limit(4).collect(Collectors.toList());
                }));
    }

    private KoiSize getRecommendedSize(FateType userFateType) {
        switch (userFateType) {
            case METAL:
            case FIRE:
                return KoiSize.LARGE;
            case WOOD:
            case EARTH:
                return KoiSize.MEDIUM;
            case WATER:
                return KoiSize.SMALL;
            default:
                throw new IllegalArgumentException("Invalid Fate");
        }
    }
}

