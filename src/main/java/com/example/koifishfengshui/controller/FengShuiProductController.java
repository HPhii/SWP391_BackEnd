package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.service.FengShuiProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FengShuiProductController {

    @Autowired
    private FengShuiProductService productService;

    // Create or update product
    @PostMapping
    public ResponseEntity<FengShuiProduct> createOrUpdateProduct(@RequestBody FengShuiProduct product) {
        FengShuiProduct savedProduct = productService.saveOrUpdateProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<FengShuiProduct>> getAllProducts() {
        List<FengShuiProduct> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<FengShuiProduct> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
