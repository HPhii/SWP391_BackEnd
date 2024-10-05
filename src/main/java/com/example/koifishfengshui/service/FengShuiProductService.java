package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.repository.FengShuiProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FengShuiProductService {

    @Autowired
    private FengShuiProductRepository productRepository;

    // Create or update product
    public FengShuiProduct saveOrUpdateProduct(FengShuiProduct product) {
        return productRepository.save(product);
    }

    public FengShuiProduct createProductFromAdRequest(AdRequest adRequest) {
        FengShuiProduct product = new FengShuiProduct();
        product.setName(adRequest.getProductName());
        product.setType(adRequest.getProductType());
        product.setCompatibleFate(adRequest.getCompatibleFate());
        product.setPrice(adRequest.getPrice());
        product.setImageUrl(adRequest.getImageUrl());
        product.setDescription(adRequest.getDescription());

        return saveOrUpdateProduct(product);
    }

    // Read all products
    public List<FengShuiProduct> getAllProducts() {
        return productRepository.findAll();
    }

    // Read product by ID
    public Optional<FengShuiProduct> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Delete product by ID
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
