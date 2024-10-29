package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity getDashboardStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
