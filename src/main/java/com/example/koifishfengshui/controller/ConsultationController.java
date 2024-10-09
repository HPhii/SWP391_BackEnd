package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.service.ConsultationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultation")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping("/get")
    public ResponseEntity<Map<String, Object>> getConsultation(@RequestParam("fate") FateType userFate) {
        Fate userFateDetails = consultationService.getUserFate(userFate);

        List<Map<String, Object>> koiResults = consultationService.getKoiRecommendations(userFate);
        List<Map<String, Object>> pondResults = consultationService.getPondRecommendations(userFate);
        List<Map<String, Object>> productResults = consultationService.getFengShuiProductRecommendations(userFate);

        Map<String, Object> response = new HashMap<>();
        response.put("Fate", userFateDetails);
        response.put("koiRecommendations", koiResults);
        response.put("pondRecommendations", pondResults);
        response.put("productRecommendations", productResults);

        return ResponseEntity.ok(response);
    }
}




