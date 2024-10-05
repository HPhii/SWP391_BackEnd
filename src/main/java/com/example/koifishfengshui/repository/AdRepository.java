package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.enums.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Advertisement, Long> {
    // Find all ads by status (e.g., PENDING, APPROVED, REJECTED)
    List<Advertisement> findByStatus(AdStatus status);

    // Custom query to find all approved ads
    List<Advertisement> findByStatusOrderByCreatedAtDesc(AdStatus status);

    // Count the number of ads posted by a user
    long countByUserUser(Long user);

//    List<Advertisement> findByUser(User user);
//
//    List<Advertisement> findByUserId(Long id);

    List<Advertisement> findByUserUser(Long user);
}
