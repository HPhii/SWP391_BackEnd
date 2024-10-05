package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.*;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.request.SubscriptionPlanRequest;
import com.example.koifishfengshui.model.response.AdResponse;
import com.example.koifishfengshui.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
    private ModelMapper modelMapper;

    @Transactional
    public AdResponse createAd(AdRequest adRequest, Authentication authentication) {
        // Get the current user
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


    public List<AdResponse> getUserAds(Authentication authentication) {
        // Get the current user
        Account account = (Account) authentication.getPrincipal();
        User user = account.getUser();

        // Retrieve ads by user
        List<Advertisement> ads = adRepository.findByUserUser(user.getUser());
        return ads.stream().map(this::mapToAdResponse).collect(Collectors.toList());
    }

    @Transactional
    public AdResponse updateAdStatus(Long adId, AdStatus status, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        ad.setStatus(status);
        adRepository.save(ad);

        // Ghi log thay đổi trạng thái quảng cáo
        logAdStatusChange(ad, status, ((Account) authentication.getPrincipal()).getUser());

        return mapToAdResponse(ad);
    }


    @Transactional
    public AdResponse selectSubscriptionPlan(Long adId, SubscriptionPlanRequest planRequest, Authentication authentication) {
        // Find the ad
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

//        // Gọi service thanh toán để thực hiện thanh toán
//        boolean paymentSuccess = paymentService.processPayment(ad, plan, ((Account) authentication.getPrincipal()).getUser());
//
//        if (paymentSuccess) {
//            // Nếu thanh toán thành công, chuyển trạng thái sang QUEUED_FOR_POST
//            ad.setStatus(AdStatus.QUEUED_FOR_POST);
//            adRepository.save(ad);
//
//            // Log status change
//            logAdStatusChange(ad, AdStatus.QUEUED_FOR_POST, ad.getUser());
//        } else {
//            // Nếu thanh toán thất bại, chuyển trạng thái sang PAYMENT_FAILED
//            ad.setStatus(AdStatus.PAYMENT_FAILED);
//            adRepository.save(ad);
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

        // Manually map fields to avoid ModelMapper ambiguity
        response.setAdId(ad.getAdId());
        response.setProductName(ad.getProductName());  // Use Advertisement's productName
        response.setProductType(ad.getProductType());  // Use Advertisement's productType
        response.setDescription(ad.getDescription());
        response.setPrice(ad.getPrice());
        response.setImageUrl(ad.getImageUrl());
        response.setContactInfo(ad.getContactInfo());
        response.setStatus(ad.getStatus());
        response.setViewsCount(ad.getViewsCount());
        response.setClicksCount(ad.getClicksCount());
        response.setCreatedAt(ad.getCreatedAt());

        // Set custom fields
        response.setUserName(ad.getUser().getFullName());  // Custom mapping for user's full name

        return response;
    }

    public long countAdsByUser(User user) {
        return adRepository.countByUserUser(user.getUser());
    }
}

