package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.request.PondFeatureRequest;
import com.example.koifishfengshui.model.response.dto.PondFeatureResponse;
import com.example.koifishfengshui.service.PondFeatureService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pondFeatures")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class PondFeatureController {

    @Autowired
    private PondFeatureService pondFeatureService;

    @PostMapping
    public ResponseEntity<PondFeatureResponse> createPondFeature(@RequestBody PondFeatureRequest pondFeatureRequest) {
        PondFeatureResponse pondFeatureResponse = pondFeatureService.createPondFeature(pondFeatureRequest);
        return new ResponseEntity<>(pondFeatureResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{pondFeatureId}")
    public ResponseEntity<PondFeatureResponse> updatePondFeature(
            @PathVariable Long pondFeatureId, @RequestBody PondFeatureRequest pondFeatureRequest) {
        PondFeatureResponse pondFeatureResponse = pondFeatureService.updatePondFeature(pondFeatureId, pondFeatureRequest);
        return new ResponseEntity<>(pondFeatureResponse, HttpStatus.OK);
    }

    @GetMapping("/{pondFeatureId}")
    public ResponseEntity<PondFeatureResponse> getPondFeatureById(@PathVariable Long pondFeatureId) {
        PondFeatureResponse pondFeatureResponse = pondFeatureService.getPondFeatureById(pondFeatureId);
        return new ResponseEntity<>(pondFeatureResponse, HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<PagedPondFeatureResponse> getAllPondFeatures(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "8") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        PagedPondFeatureResponse response = pondFeatureService.getAllPondFeatures(pageable);
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{pondFeatureId}")
    public ResponseEntity<Void> deletePondFeature(@PathVariable Long pondFeatureId) {
        pondFeatureService.deletePondFeature(pondFeatureId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
