package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.enums.FateType;
import com.example.koifishfengshui.service.FateCalculationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fate")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FateController {

    @Autowired
    private FateCalculationService fateCalculationService;

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateFate(@RequestParam("birthdate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthdate) {
        FateType userFate = fateCalculationService.calculateFate(birthdate);

        Map<String, Object> response = new HashMap<>();
        response.put("userFate", userFate);

        return ResponseEntity.ok(response);
    }
}


