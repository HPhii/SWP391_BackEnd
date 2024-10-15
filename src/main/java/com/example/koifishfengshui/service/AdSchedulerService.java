package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.AdStatus;
import com.example.koifishfengshui.model.entity.Advertisement;
import com.example.koifishfengshui.model.entity.User;
import com.example.koifishfengshui.model.response.dto.EmailDetails;
import com.example.koifishfengshui.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdSchedulerService {

    @Autowired
    private AdRepository advertisementRepository;

    @Autowired
    private EmailService emailService;

    private static final String EXPIRED_TEMPLATE = "ad-expiration-template";

    @Scheduled(cron = "0 0/15 * * * ?")
    @Transactional
    public void updateExpiredAds() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Advertisement> expiredAdsPage;
        do {
            expiredAdsPage = advertisementRepository.findAllExpiredAds(pageable);
            for (Advertisement ad : expiredAdsPage.getContent()) {
                ad.setStatus(AdStatus.EXPIRED);
                sendExpirationEmail(ad);
            }
            advertisementRepository.saveAll(expiredAdsPage.getContent());
        } while (expiredAdsPage.hasNext());
    }

    private void sendExpirationEmail(Advertisement ad) {
        User user = ad.getUser();

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(user.getAccount());
        emailDetails.setSubject("Your Advertisement has Expired - " + ad.getProductName());

        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("name", user.getFullName());
        contextVariables.put("productName", ad.getProductName());
        contextVariables.put("adId", ad.getAdId());

        emailService.sendMail(emailDetails, EXPIRED_TEMPLATE, contextVariables);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationStart() {
        updateExpiredAds();
    }
}

