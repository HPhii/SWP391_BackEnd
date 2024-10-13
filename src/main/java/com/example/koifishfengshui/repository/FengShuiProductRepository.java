package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FengShuiProductRepository extends JpaRepository<FengShuiProduct, Long> {

    Page<FengShuiProduct> findAll(Pageable pageable);

    // Find a product by name
    Optional<FengShuiProduct> findByName(String name);

    // Find all products by a specific ProductType
    List<FengShuiProduct> findByType(ProductType type);

    List<FengShuiProduct> findByCompatibleFate(Fate fate);

    // Find products compatible with a specific fate
    List<FengShuiProduct> findByCompatibleFate(FateType fate);

    // Find products by fate and type
    List<FengShuiProduct> findByCompatibleFateAndType(FateType fate, ProductType type);
}

