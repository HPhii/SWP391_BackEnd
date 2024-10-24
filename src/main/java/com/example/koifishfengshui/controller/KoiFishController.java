package com.example.koifishfengshui.controller;

import com.example.koifishfengshui.model.request.KoiFishRequest;
import com.example.koifishfengshui.model.response.dto.KoiFishResponse;
import com.example.koifishfengshui.model.response.paged.PagedKoiFishResponse;
import com.example.koifishfengshui.service.KoiFishService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/koiFish")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class KoiFishController {

    @Autowired
    private KoiFishService koiFishService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<KoiFishResponse> createKoiFish(@ModelAttribute KoiFishRequest koiFishRequest) {
        KoiFishResponse koiFishResponse = koiFishService.createKoiFish(koiFishRequest);
        return new ResponseEntity<>(koiFishResponse, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{koiId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<KoiFishResponse> updateKoiFish(
            @PathVariable Long koiId, @ModelAttribute KoiFishRequest koiFishRequest) {
        KoiFishResponse koiFishResponse = koiFishService.updateKoiFish(koiId, koiFishRequest);
        return new ResponseEntity<>(koiFishResponse, HttpStatus.OK);
    }

    @GetMapping("/{koiId}")
    public ResponseEntity<KoiFishResponse> getKoiFishById(@PathVariable Long koiId) {
        KoiFishResponse koiFishResponse = koiFishService.getKoiFishById(koiId);
        return new ResponseEntity<>(koiFishResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedKoiFishResponse> getAllKoiFish(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedKoiFishResponse response = koiFishService.getAllKoiFish(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{koiId}")
    public ResponseEntity<Void> deleteKoiFish(@PathVariable Long koiId) {
        koiFishService.deleteKoiFish(koiId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
