package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.model.request.AdRequest;
import com.example.koifishfengshui.model.request.SubscriptionPlanRequest;
import com.example.koifishfengshui.model.response.AdResponse;
import com.example.koifishfengshui.service.AdService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AdsController {

    @Autowired
    private AdService adService;

    @PostMapping
    public ResponseEntity<AdResponse> createAd(@RequestBody AdRequest adRequest, Authentication authentication) {
        AdResponse adResponse = adService.createAd(adRequest, authentication);
        return ResponseEntity.ok(adResponse);
    }

    @GetMapping("/my-ads")
    public ResponseEntity<List<AdResponse>> getUserAds(Authentication authentication) {
        List<AdResponse> ads = adService.getUserAds(authentication);
        return ResponseEntity.ok(ads);
    }

    @PutMapping("/{adId}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdResponse> approveAd(@PathVariable Long adId, @RequestParam AdStatus status, Authentication authentication) {
        AdResponse adResponse = adService.updateAdStatus(adId, status, authentication);
        return ResponseEntity.ok(adResponse);
    }


    @PostMapping("/{adId}/select-plan")
    public ResponseEntity<AdResponse> selectSubscriptionPlan(@PathVariable Long adId, @RequestBody SubscriptionPlanRequest planRequest, Authentication authentication) {
        AdResponse adResponse = adService.selectSubscriptionPlan(adId, planRequest, authentication );
        return ResponseEntity.ok(adResponse);
    }

    @PutMapping("/{adId}/final-approval")
    public ResponseEntity<AdResponse> userFinalApproval(@PathVariable Long adId, Authentication authentication) {
        AdResponse adResponse = adService.userFinalApproval(adId, authentication);
        return ResponseEntity.ok(adResponse);
    }
}

