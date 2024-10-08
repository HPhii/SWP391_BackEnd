package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.model.entity.Fate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FateRepository extends JpaRepository<Fate, Long> {
    Optional<Fate> findByFateType(FateType compatibleFate);
}
