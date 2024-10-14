package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByStatus(AdStatus status);

    long countByUserUser(Long user);

    Page<Advertisement> findAll(Pageable pageable);

    @Query("SELECT a FROM Advertisement a " +
            "JOIN a.subscriptionPlan sp " +
            "WHERE sp.duration > 0 " +
            "AND a.status = 'PUBLISHED'" +
            "ORDER BY " +
            "CASE sp.adPlacementPriority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "END ASC, " +
            "a.createdAt DESC")
    Page<Advertisement> findActiveAds(Pageable pageable);

    Page<Advertisement> findByStatus(AdStatus status, Pageable pageable);

    Page<Advertisement> findByUserUser(Long user, Pageable pageable);
}
