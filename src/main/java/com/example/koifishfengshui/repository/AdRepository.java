package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.model.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByStatus(AdStatus status);

    long countByUserUser(Long user);

    Page<Advertisement> findAll(Pageable pageable);

    @Query("SELECT a FROM Advertisement a " +
            "JOIN a.subscriptionPlan sp " +
            "WHERE a.status = 'PUBLISHED' " +
            "AND CURRENT_TIMESTAMP BETWEEN a.startDate AND a.endDate " +
            "ORDER BY " +
            "CASE WHEN sp.adPlacementPriority = 'HIGH' THEN 3 " +
            "WHEN sp.adPlacementPriority = 'MEDIUM' THEN 2 " +
            "WHEN sp.adPlacementPriority = 'LOW' THEN 1 " +
            "ELSE 0 END DESC, " +
            "a.createdAt DESC")
    Page<Advertisement> findActiveAds(Pageable pageable);

    Page<Advertisement> findByStatus(AdStatus status, Pageable pageable);

    Page<Advertisement> findByUserUser(Long user, Pageable pageable);

    @Query("SELECT a FROM Advertisement a " +
            "WHERE a.endDate < CURRENT_TIMESTAMP " +
            "AND a.status <> 'EXPIRED'")
    Page<Advertisement> findAllExpiredAds(Pageable pageable);
}
