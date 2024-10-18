package com.example.koifishfengshui.service;

import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.model.entity.FengShuiProduct;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.response.paged.PagedProductResponse;
import com.example.koifishfengshui.repository.FateRepository;
import com.example.koifishfengshui.repository.FengShuiProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FengShuiProductService {

    @Autowired
    private FengShuiProductRepository productRepository;

    @Autowired
    private FateRepository fateRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public FengShuiProduct createProductFromAdRequest(AdRequest adRequest) {
        Map uploadResult = cloudinaryService.upload(adRequest.getImageFile());
        String imageUrl = (String) uploadResult.get("secure_url");

        FengShuiProduct product = new FengShuiProduct();
        product.setName(adRequest.getProductName());
        product.setType(adRequest.getProductType());

        Fate compatibleFate = fateRepository.findByFateType(adRequest.getCompatibleFate())
                .orElseThrow(() -> new EntityNotFoundException("Fate not found!"));

        product.setCompatibleFate(compatibleFate);

        product.setPrice(adRequest.getPrice());
        product.setImageUrl(imageUrl);
        product.setDescription(adRequest.getDescription());

        return saveOrUpdateProduct(product);
    }

    public FengShuiProduct saveOrUpdateProduct(FengShuiProduct product) {
        return productRepository.save(product);
    }

    // Read all products
    @Transactional
    public PagedProductResponse getAllProducts(Pageable pageable) {
        Page<FengShuiProduct> productPage = productRepository.findAll(pageable);

        List<FengShuiProduct> products = productPage.getContent();

        return new PagedProductResponse(
                products,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                pageable.getPageNumber()
        );
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
