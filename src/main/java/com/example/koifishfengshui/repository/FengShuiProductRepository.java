package com.example.koifishfengshui.repository;

import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.enums.Fate;
import com.example.koifishfengshui.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FengShuiProductRepository extends JpaRepository<FengShuiProduct, Long> {

    // Find a product by name
    Optional<FengShuiProduct> findByName(String name);

    // Find all products by a specific ProductType
    List<FengShuiProduct> findByType(ProductType type);

    // Find products compatible with a specific fate
    List<FengShuiProduct> findByCompatibleFate(Fate fate);

    // Find products by fate and type
    List<FengShuiProduct> findByCompatibleFateAndType(Fate fate, ProductType type);
}
