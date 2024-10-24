package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.PondFeature;
import com.example.koifishfengshui.model.request.PondFeatureRequest;
import com.example.koifishfengshui.model.response.dto.PondFeatureResponse;
import com.example.koifishfengshui.repository.FateRepository;
import com.example.koifishfengshui.repository.PondFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PondFeatureService {

    @Autowired
    private PondFeatureRepository pondFeatureRepository;

    @Autowired
    private FateRepository fateRepository;

    public PondFeatureResponse createPondFeature(PondFeatureRequest requestDTO) {
        PondFeature pondFeature = new PondFeature();
        pondFeature.setShape(requestDTO.getShape());
        pondFeature.setPlacement(requestDTO.getPlacement());
        pondFeature.setDirection(requestDTO.getDirection());
        pondFeature.setDescription(requestDTO.getDescription());

        Fate fate = fateRepository.findByFateType(requestDTO.getCompatibleFateType())
                .orElseThrow(() -> new RuntimeException("Fate not found"));
        pondFeature.setCompatibleFate(fate);

        pondFeature = pondFeatureRepository.save(pondFeature);

        return mapToResponseDTO(pondFeature);
    }

    public PondFeatureResponse updatePondFeature(Long pondFeatureId, PondFeatureRequest requestDTO) {
        PondFeature pondFeature = pondFeatureRepository.findById(pondFeatureId)
                .orElseThrow(() -> new RuntimeException("PondFeature not found"));

        if (requestDTO.getShape() != null) pondFeature.setShape(requestDTO.getShape());
        if (requestDTO.getPlacement() != null) pondFeature.setPlacement(requestDTO.getPlacement());
        if (requestDTO.getDirection() != null) pondFeature.setDirection(requestDTO.getDirection());
        if (requestDTO.getDescription() != null) pondFeature.setDescription(requestDTO.getDescription());

        Fate fate = fateRepository.findByFateType(requestDTO.getCompatibleFateType())
                .orElseThrow(() -> new RuntimeException("Fate not found"));
        pondFeature.setCompatibleFate(fate);

        pondFeature = pondFeatureRepository.save(pondFeature);

        return mapToResponseDTO(pondFeature);
    }

//    @Transactional
//    public PagedPondFeatureResponse getAllPondFeatures(Pageable pageable) {
//        Page<PondFeature> pondFeaturePage = pondFeatureRepository.findAll(pageable);
//        List<PondFeatureResponse> pondFeatureList = pondFeaturePage.getContent()
//                .stream()
//                .map(this::mapToResponseDTO)
//                .collect(Collectors.toList());
//
//        return new PagedPondFeatureResponse(
//                pondFeatureList,
//                pondFeaturePage.getTotalElements(),
//                pondFeaturePage.getTotalPages(),
//                pageable.getPageNumber()
//        );
//    }

    public PondFeatureResponse getPondFeatureById(Long pondFeatureId) {
        PondFeature pondFeature = pondFeatureRepository.findById(pondFeatureId)
                .orElseThrow(() -> new RuntimeException("PondFeature not found"));
        return mapToResponseDTO(pondFeature);
    }

    public void deletePondFeature(Long pondFeatureId) {
        pondFeatureRepository.deleteById(pondFeatureId);
    }

    private PondFeatureResponse mapToResponseDTO(PondFeature pondFeature) {
        PondFeatureResponse pondFeatureResponse = new PondFeatureResponse();
        pondFeatureResponse.setPondFeatureId(pondFeature.getPondFeatureId());
        pondFeatureResponse.setShape(pondFeature.getShape());
        pondFeatureResponse.setPlacement(pondFeature.getPlacement());
        pondFeatureResponse.setDirection(pondFeature.getDirection());
        pondFeatureResponse.setCompatibleFateType(pondFeature.getCompatibleFate().getFateType());
        pondFeatureResponse.setDescription(pondFeature.getDescription());

        return pondFeatureResponse;
    }
}

