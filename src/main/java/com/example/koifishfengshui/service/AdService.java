package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.enums.PaymentStatus;
import com.example.koifishfengshui.exception.EntityNotFoundException;
import com.example.koifishfengshui.model.entity.*;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.request.SubscriptionPlanRequest;
import com.example.koifishfengshui.model.response.dto.AdResponse;
import com.example.koifishfengshui.model.response.dto.EmailDetails;
import com.example.koifishfengshui.model.response.paged.PagedAdResponse;
import com.example.koifishfengshui.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TransactionHistoryRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    private static final String CONFIRMATION_TEMPLATE = "order-confirmation-template";

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

    public PagedAdResponse getActiveAds(Pageable pageable) {
        Page<Advertisement> adsPage = adRepository.findActiveAds(pageable);

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
    public PagedAdResponse getAdsByStatus(AdStatus status, Pageable pageable) {
        Page<Advertisement> adsPage = adRepository.findByStatus(status, pageable);

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

    public AdResponse getAdById(Long adId) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        return mapToAdResponse(ad);
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
    public String selectSubscriptionPlan(Long adId, SubscriptionPlanRequest planRequest, Authentication authentication) throws Exception {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found."));

        validateAdStatusForSubscription(ad);

        SubscriptionPlan plan = planRepository.findById(planRequest.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid subscription plan."));

        ad.setSubscriptionPlan(plan);
        ad.setStatus(AdStatus.PENDING_PAYMENT);
        adRepository.save(ad);

        logAdStatusChange(ad, AdStatus.PENDING_PAYMENT, ad.getUser());

        TransactionHistory transaction = transactionService.createTransaction(ad.getUser(), ad, plan.getPrice());
        return paymentService.createUrl(transaction, plan);
    }

    private void validateAdStatusForSubscription(Advertisement ad) {
        switch (ad.getStatus()) {
            case PENDING:
                throw new IllegalStateException("The ad is still pending approval. Please wait for admin approval.");
            case REJECTED:
                throw new IllegalStateException("The ad has been rejected. You cannot select a subscription plan.");
            case PENDING_PAYMENT:
                throw new IllegalStateException("The ad is already pending payment. Complete the payment before selecting another plan.");
            case PAYMENT_FAILED:
                throw new IllegalStateException("Previous payment failed. Please retry the payment.");
            case QUEUED_FOR_POST:
                throw new IllegalStateException("The ad is queued for posting. You cannot change the subscription plan now.");
            case PUBLISHED:
                throw new IllegalStateException("The ad has been published. Changing subscription plans is not allowed.");
            case APPROVED:
                break;
            default:
                throw new IllegalStateException("Unexpected ad status. Please contact support.");
        }
    }


//    public void handlePaymentResponse(Long transactionId, boolean success) {
//        TransactionHistory transaction = transactionRepository.findById(transactionId)
//                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
//
//        Advertisement ad = transaction.getAdvertisement();
//
//        if (success) {
//            transaction.setPaymentStatus(PaymentStatus.SUCCESS);
//            ad.setStatus(AdStatus.QUEUED_FOR_POST);
//
//            sendOrderConfirmationEmail(transaction);
//        } else {
//            transaction.setPaymentStatus(PaymentStatus.FAILED);
//            ad.setStatus(AdStatus.PAYMENT_FAILED);
//        }
//
//        adRepository.save(ad);
//        transactionRepository.save(transaction);
//
//        logAdStatusChange(ad, ad.getStatus(), ad.getUser());
//    }

    public Advertisement handlePaymentResponse(Long transactionId, boolean success) {
        TransactionHistory transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Advertisement ad = transaction.getAdvertisement();

        if (success) {
            transaction.setPaymentStatus(PaymentStatus.SUCCESS);
            ad.setStatus(AdStatus.QUEUED_FOR_POST);

            sendOrderConfirmationEmail(transaction);
        } else {
            transaction.setPaymentStatus(PaymentStatus.FAILED);
            ad.setStatus(AdStatus.PAYMENT_FAILED);
        }

        adRepository.save(ad);
        transactionRepository.save(transaction);

        logAdStatusChange(ad, ad.getStatus(), ad.getUser());

        return ad;
    }

    private void sendOrderConfirmationEmail(TransactionHistory transaction) {
        User user = transaction.getUser();

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(user.getAccount());
        emailDetails.setSubject("Order Confirmation - Transaction #" + transaction.getTransactionId());

        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("name", user.getFullName());
        contextVariables.put("transactionId", transaction.getTransactionId());
        contextVariables.put("amount", transaction.getAmount());
        contextVariables.put("paymentMethod", transaction.getPaymentMethod());

        emailService.sendMail(emailDetails, CONFIRMATION_TEMPLATE, contextVariables);
    }


    @Transactional
    public AdResponse userFinalApproval(Long adId, Authentication authentication) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        if (ad.getStatus() != AdStatus.QUEUED_FOR_POST) {
            throw new IllegalStateException("Ads is not ready for user approval.");
        }
        ad.setStartDate(LocalDateTime.now());
        ad.setDurationInDays(ad.getSubscriptionPlan().getDuration());
        ad.setEndDate(ad.getStartDate().plusDays(ad.getDurationInDays()));
        ad.setStatus(AdStatus.PUBLISHED);
        adRepository.save(ad);

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
        response.setCreatedAt(ad.getCreatedAt());
        response.setUserName(ad.getUser().getFullName());
        if (ad.getEndDate() != null) {
            response.setDaysLeft(Duration.between(LocalDateTime.now(), ad.getEndDate()).toDays());
        } else {
            response.setDaysLeft(0L);
        }

        return response;
    }

    public long countAdsByUser(User user) {
        return adRepository.countByUserUser(user.getUser());
    }
}

