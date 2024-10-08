package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.PondFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PondFeatureRepository extends JpaRepository<PondFeature, Long> {
    List<PondFeature> findByCompatibleFate(FateType compatibleFate);
    List<PondFeature> findByCompatibleFate(Fate fate);

}

