package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.*;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.request.SubscriptionPlanRequest;
import com.example.koifishfengshui.model.response.dto.AdResponse;
import com.example.koifishfengshui.model.response.paged.PagedAdResponse;
import com.example.koifishfengshui.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private FengShuiProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Autowired
    private FengShuiProductService fengShuiProductService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AdStatusLogRepository adStatusLogRepository;

    @Autowired
    private FengShuiProductRepository fengShuiProductRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public AdResponse createAd(AdRequest adRequest, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        User user = account.getUser();

        FengShuiProduct product = fengShuiProductService.createProductFromAdRequest(adRequest);

        Advertisement ad = createAdFromRequest(adRequest, user, product);
        adRepository.save(ad);

        logAdStatusChange(ad, AdStatus.PENDING, user);
        return mapToAdResponse(ad);
    }

    private static Advertisement createAdFromRequest(AdRequest adRequest, User user, FengShuiProduct product) {
        Advertisement ad = new Advertisement();
        ad.setUser(user);
        ad.setProductName(adRequest.getProductName());
        ad.setProductType(adRequest.getProductType());
        ad.setDescription(adRequest.getDescription());
        ad.setPrice(adRequest.getPrice());
        ad.setImageUrl(adRequest.getImageUrl());
        ad.setContactInfo(adRequest.getContactInfo());
        ad.setProduct(product);
        ad.setStatus(AdStatus.PENDING);
        return ad;
    }

    @Transactional
    public AdResponse updateAds(Long adId, AdRequest adRequest, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ads not found"));

        if (ad.getStatus() == AdStatus.PUBLISHED || ad.getStatus() == AdStatus.QUEUED_FOR_POST || ad.getStatus() == AdStatus.PENDING_PAYMENT || ad.getStatus() == AdStatus.PAYMENT_FAILED) {
            throw new IllegalStateException("Your ads are in " + ad.getStatus() + "status, cannot edit!!!");
        }

        if (adRequest.getProductName() != null) ad.setProductName(adRequest.getProductName());
        if (adRequest.getProductType() != null) ad.setProductType(adRequest.getProductType());
        if (adRequest.getDescription() != null) ad.setDescription(adRequest.getDescription());
        if (adRequest.getPrice() != null) ad.setPrice(adRequest.getPrice());
        if (adRequest.getImageUrl() != null) ad.setImageUrl(adRequest.getImageUrl());
        if (adRequest.getContactInfo() != null) ad.setContactInfo(adRequest.getContactInfo());

        ad.setStatus(AdStatus.PENDING);
        ad.setUpdatedAt(LocalDateTime.now());

        FengShuiProduct updatedProduct = fengShuiProductService.createProductFromAdRequest(adRequest);
        ad.setProduct(updatedProduct);

        adRepository.save(ad);
        logAdStatusChange(ad, AdStatus.PENDING, ((Account) authentication.getPrincipal()).getUser());
        return mapToAdResponse(ad);
    }

    public PagedAdResponse getUserAds(Authentication authentication, Pageable pageable) {
        Account account = (Account) authentication.getPrincipal();
        User user = account.getUser();

        Page<Advertisement> adsPage = adRepository.findByUserUser(user.getUser(), pageable);

        List<AdResponse> adResponses = adsPage.getContent()
                .stream()
                .map(this::mapToAdResponse)
                .collect(Collectors.toList());

        return new PagedAdResponse(
                adResponses,
                adsPage.getTotalElements(),
                adsPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }

    public PagedAdResponse getAllAds(Pageable pageable) {
        Page<Advertisement> adsPage = adRepository.findAll(pageable);

        List<AdResponse> adResponses = adsPage.getContent()
                .stream()
                .map(this::mapToAdResponse)
                .collect(Collectors.toList());

        return new PagedAdResponse(
                adResponses,
                adsPage.getTotalElements(),
                adsPage.getTotalPages(),
                pageable.getPageNumber()
        );
    }

    @Transactional
    public AdResponse updateAdStatus(Long adId, AdStatus status, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        ad.setStatus(status);
        adRepository.save(ad);

        logAdStatusChange(ad, status, ((Account) authentication.getPrincipal()).getUser());

        return mapToAdResponse(ad);
    }


    @Transactional
    public AdResponse selectSubscriptionPlan(Long adId, SubscriptionPlanRequest planRequest, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ads not found"));

        if (ad.getStatus() != AdStatus.APPROVED) {
            throw new IllegalStateException("You cannot select a subscription plan until the ads is approved by admin.");
        }

        // Find the selected subscription plan
        SubscriptionPlan plan = planRepository.findById(planRequest.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid subscription plan"));

        // Link the ad with the subscription plan
        ad.setSubscriptionPlan(plan);
//        ad.setStatus(AdStatus.PENDING_PAYMENT);
        ad.setStatus(AdStatus.QUEUED_FOR_POST);
        adRepository.save(ad);

        // Log the status change
//        logAdStatusChange(ad, AdStatus.PENDING_PAYMENT, ad.getUser());
        logAdStatusChange(ad, AdStatus.QUEUED_FOR_POST, ad.getUser());

        TransactionHistory transaction = transactionService.createTransaction(ad.getUser(), plan.getPrice());

        // Gọi service thanh toán để thực hiện thanh toán
        boolean paymentSuccess = paymentService.processPayment(ad, plan, ((Account) authentication.getPrincipal()).getUser());

//        if (paymentSuccess) {
//            // Nếu thanh toán thành công, chuyển trạng thái sang QUEUED_FOR_POST
//            ad.setStatus(AdStatus.QUEUED_FOR_POST);
//            adRepository.save(ad);
//
//            transactionService.updateTransactionStatus(transaction.getTransactionId(), PaymentStatus.SUCCESS);
//
//            // Log status change
//            logAdStatusChange(ad, AdStatus.QUEUED_FOR_POST, ad.getUser());
//        } else {
//            // Nếu thanh toán thất bại, chuyển trạng thái sang PAYMENT_FAILED
//            ad.setStatus(AdStatus.PAYMENT_FAILED);
//            adRepository.save(ad);
//
//            transactionService.updateTransactionStatus(transaction.getTransactionId(), PaymentStatus.FAILED);
//
//            // Log status change
//            logAdStatusChange(ad, AdStatus.PAYMENT_FAILED, ad.getUser());
//        }

        return mapToAdResponse(ad);
    }

    @Transactional
    public AdResponse userFinalApproval(Long adId, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        // Check if the ad is ready for final approval
        if (ad.getStatus() != AdStatus.QUEUED_FOR_POST) {
            throw new IllegalStateException("Ads is not ready for user approval.");
        }

        // Update ad status to PUBLISHED
        ad.setStatus(AdStatus.PUBLISHED);
        adRepository.save(ad);

        // Log the status change
        logAdStatusChange(ad, AdStatus.PUBLISHED, ad.getUser());

        return mapToAdResponse(ad);
    }

    private void logAdStatusChange(Advertisement ad, AdStatus status, User user) {
        AdStatusLog log = new AdStatusLog();
        log.setAdvertisement(ad);
        log.setStatus(status);
        log.setChangedBy(user);
        adStatusLogRepository.save(log);
    }

    private AdResponse mapToAdResponse(Advertisement ad) {
        AdResponse response = new AdResponse();

        response.setAdId(ad.getAdId());
        response.setProductName(ad.getProductName());
        response.setProductType(ad.getProductType());
        response.setDescription(ad.getDescription());
        response.setPrice(ad.getPrice());
        response.setImageUrl(ad.getImageUrl());
        response.setContactInfo(ad.getContactInfo());
        response.setStatus(ad.getStatus());
        response.setViewsCount(ad.getViewsCount());
        response.setClicksCount(ad.getClicksCount());
        response.setCreatedAt(ad.getCreatedAt());

        response.setUserName(ad.getUser().getFullName());

        return response;
    }

    public long countAdsByUser(User user) {
        return adRepository.countByUserUser(user.getUser());
    }
}

