package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.enums.AdStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByStatus(AdStatus status);

    long countByUserUser(Long user);

    Page<Advertisement> findAll(Pageable pageable);

    Page<Advertisement> findByUserUser(Long user, Pageable pageable);
}
