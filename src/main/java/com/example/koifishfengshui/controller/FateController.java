package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.entity.Fate;
import com.example.koifishfengshui.service.FateCalculationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/fate")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FateController {

    @Autowired
    private FateCalculationService fateCalculationService;

    @GetMapping("/calculate")
    public ResponseEntity<Fate> calculateFate(@RequestParam("birthdate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthdate) {
        Fate fate = fateCalculationService.calculateFate(birthdate);
        return ResponseEntity.ok(fate);
    }
}


