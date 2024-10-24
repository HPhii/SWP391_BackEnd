package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.request.SubscriptionPlanRequest;
import com.example.koifishfengshui.model.response.dto.AdResponse;
import com.example.koifishfengshui.model.response.paged.PagedAdResponse;
import com.example.koifishfengshui.service.AdService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AdsController {

    @Autowired
    private AdService adService;

    // Create a new advertisement
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<AdResponse> createAd(@ModelAttribute AdRequest adRequest, Authentication authentication) {
        AdResponse adResponse = adService.createAd(adRequest, authentication);
        return new ResponseEntity<>(adResponse, HttpStatus.CREATED);
    }

    // Get ads of the authenticated user
    @GetMapping("/my")
    public ResponseEntity<PagedAdResponse> getUserAds(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedAdResponse response = adService.getUserAds(authentication, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedAdResponse> getAllAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedAdResponse response = adService.getAllAds(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedAdResponse> getActiveAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        PagedAdResponse response = adService.getActiveAds(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<PagedAdResponse> getAdsByStatus(
            @RequestParam AdStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        PagedAdResponse response = adService.getAdsByStatus(status, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{adId}")
    public ResponseEntity<AdResponse> getAdById(@PathVariable Long adId) {
        AdResponse adResponse = adService.getAdById(adId);
        return ResponseEntity.ok(adResponse);
    }

    // Admin approves or rejects an advertisement
    @PutMapping("/{adId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdResponse> updateAdStatus(@PathVariable Long adId, @RequestParam AdStatus status, Authentication authentication) {
        AdResponse adResponse = adService.updateAdStatus(adId, status, authentication);
        return ResponseEntity.ok(adResponse);
    }

    // Update a rejected advertisement
    @PutMapping("/{adId}")
    public ResponseEntity<AdResponse> updateRejectedAd(@PathVariable Long adId, @RequestBody AdRequest adRequest, Authentication authentication) {
        AdResponse adResponse = adService.updateAds(adId, adRequest, authentication);
        return ResponseEntity.ok(adResponse);
    }

    // User selects a subscription plan for their ad
    @PostMapping("/{adId}/subscription")
    public ResponseEntity<String> selectSubscriptionPlan(@PathVariable Long adId, @RequestBody SubscriptionPlanRequest planRequest, Authentication authentication) throws Exception {
        String paymentUrl = adService.selectSubscriptionPlan(adId, planRequest, authentication);
        return ResponseEntity.ok(paymentUrl);
    }

    // User gives final approval for their ad
    @PutMapping("/{adId}/approval")
    public ResponseEntity<AdResponse> userFinalApproval(@PathVariable Long adId, Authentication authentication) {
        AdResponse adResponse = adService.userFinalApproval(adId, authentication);
        return ResponseEntity.ok(adResponse);
    }

    @PutMapping("/{adId}/renew")
    public ResponseEntity<String> renewAd(@PathVariable Long adId) throws Exception {
        String paymentUrl = adService.renewAd(adId);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Map<String, Object>> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");

        Long transactionId;
        try {
            transactionId = Long.parseLong(txnRef);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid transaction reference"));
        }

        if ("00".equals(status)) {
            Advertisement ad = adService.handlePaymentResponse(transactionId, true);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment successful");
            response.put("adId", ad.getAdId());
            response.put("redirectUrl", "ads/adId=" + ad.getAdId());
            return ResponseEntity.ok(response);
        } else {
            adService.handlePaymentResponse(transactionId, false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Payment failed"));
        }
    }
}

