package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.FateType;
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

    public List<Map<String, Object>> getKoiRecommendations(FateType userFateType) {
        // Tìm Fate dựa trên FateType
        Fate userFate = fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new RuntimeException("Fate not found for type: " + userFateType));

        List<KoiFish> koiRecommendations = koiFishRepository.findByCompatibleFate(userFate);
        return koiRecommendations.stream().map(koi -> {
            Map<String, Object> koiResult = new HashMap<>();
            koiResult.put("koi", koi);
            koiResult.put("compatibilityRate", compatibilityService.calculateKoiCompatibility(userFateType, koi));
            return koiResult;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPondRecommendations(FateType userFateType) {
        // Tìm Fate dựa trên FateType
        Fate userFate = fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new RuntimeException("Fate not found for type: " + userFateType));

        List<PondFeature> pondRecommendations = pondFeatureRepository.findByCompatibleFate(userFate);
        return pondRecommendations.stream().map(pond -> {
            Map<String, Object> pondResult = new HashMap<>();
            pondResult.put("pond", pond);
            pondResult.put("compatibilityRate", compatibilityService.calculatePondCompatibility(userFateType, pond));
            return pondResult;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getFengShuiProductRecommendations(FateType userFateType) {
        // Tìm Fate dựa trên FateType
        Fate userFate = fateRepository.findByFateType(userFateType)
                .orElseThrow(() -> new RuntimeException("Fate not found for type: " + userFateType));

        List<FengShuiProduct> productRecommendations = fengShuiProductRepository.findByCompatibleFate(userFate)
                .stream().limit(4).collect(Collectors.toList());
        return productRecommendations.stream().map(product -> {
            Map<String, Object> productResult = new HashMap<>();
            productResult.put("product", product);
            productResult.put("compatibilityRate", compatibilityService.calculateProductCompatibility(userFateType, product));
            return productResult;
        }).collect(Collectors.toList());
    }
}
