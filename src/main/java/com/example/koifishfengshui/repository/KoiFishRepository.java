package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.KoiFish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KoiFishRepository extends JpaRepository<KoiFish, Long> {
    List<KoiFish> findByCompatibleFate(FateType compatibleFate);
    List<KoiFish> findByCompatibleFate(Fate fate);

}

