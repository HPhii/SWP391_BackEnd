package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm Category theo tên (nếu cần sử dụng trong tương lai)
    Optional<Category> findByCategoryName(String categoryName);
}
