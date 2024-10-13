package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.KoiFish;
import com.example.koifishfengshui.model.request.KoiFishRequest;
import com.example.koifishfengshui.model.response.dto.KoiFishResponse;
import com.example.koifishfengshui.model.response.paged.PagedKoiFishResponse;
import com.example.koifishfengshui.repository.FateRepository;
import com.example.koifishfengshui.repository.KoiFishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KoiFishService {

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private FateRepository fateRepository;

    public KoiFishResponse createKoiFish(KoiFishRequest requestDTO) {
        KoiFish koiFish = new KoiFish();
        koiFish.setSpecies(requestDTO.getSpecies());
        koiFish.setColor(requestDTO.getColor());
        koiFish.setSize(requestDTO.getSize());
        koiFish.setPrice(requestDTO.getPrice());
        koiFish.setImageUrl(requestDTO.getImageUrl());
        koiFish.setDescription(requestDTO.getDescription());
//        koiFish.setColorFate(requestDTO.getColorFate());

        Fate fate = fateRepository.findByFateType(requestDTO.getCompatibleFateType())
                .orElseThrow(() -> new RuntimeException("Fate not found"));
        koiFish.setCompatibleFate(fate);

        koiFish = koiFishRepository.save(koiFish);

        return mapToResponseDTO(koiFish);
    }

    public KoiFishResponse updateKoiFish(Long koiId, KoiFishRequest requestDTO) {
        KoiFish koiFish = koiFishRepository.findById(koiId)
                .orElseThrow(() -> new RuntimeException("KoiFish not found"));

        if (requestDTO.getSpecies() != null) koiFish.setSpecies(requestDTO.getSpecies());
        if (requestDTO.getColor() != null) koiFish.setColor(requestDTO.getColor());
        if (requestDTO.getSize() != null) koiFish.setSize(requestDTO.getSize());
        if (requestDTO.getPrice() == 0) koiFish.setPrice(requestDTO.getPrice());
        if (requestDTO.getImageUrl() != null) koiFish.setImageUrl(requestDTO.getImageUrl());
        if (requestDTO.getDescription() != null) koiFish.setDescription(requestDTO.getDescription());

        Fate fate = fateRepository.findByFateType(requestDTO.getCompatibleFateType())
                .orElseThrow(() -> new RuntimeException("Fate not found"));
        koiFish.setCompatibleFate(fate);

        koiFish = koiFishRepository.save(koiFish);

        return mapToResponseDTO(koiFish);
    }

    @Transactional
    public PagedKoiFishResponse getAllKoiFish(Pageable pageable) {
        Page<KoiFish> koiFishPage = koiFishRepository.findAll(pageable);
        List<KoiFishResponse> koiFishList = koiFishPage.getContent()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return new PagedKoiFishResponse(
                koiFishList,
                koiFishPage.getTotalElements(),
                koiFishPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }

    public KoiFishResponse getKoiFishById(Long koiId) {
        KoiFish koiFish = koiFishRepository.findById(koiId)
                .orElseThrow(() -> new RuntimeException("KoiFish not found"));
        return mapToResponseDTO(koiFish);
    }

    public void deleteKoiFish(Long koiId) {
        koiFishRepository.deleteById(koiId);
    }

    private KoiFishResponse mapToResponseDTO(KoiFish koiFish) {
        KoiFishResponse koiFishResponse = new KoiFishResponse();
        koiFishResponse.setKoiId(koiFish.getKoiId());
        koiFishResponse.setSpecies(koiFish.getSpecies());
        koiFishResponse.setColor(koiFish.getColor());
        koiFishResponse.setSize(koiFish.getSize());
        koiFishResponse.setPrice(koiFish.getPrice());
        koiFishResponse.setCompatibleFateType(koiFish.getCompatibleFate().getFateType());
        koiFishResponse.setImageUrl(koiFish.getImageUrl());
        koiFishResponse.setDescription(koiFish.getDescription());

        return koiFishResponse;
    }
}

