package com.news.aggregator.controller;

import com.news.aggregator.dto.PreferenceRequest;
import com.news.aggregator.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    // POST request ke liye explicitly handle karega (Creation)
    @PostMapping
    public ResponseEntity<String> createPreferences(@Valid @RequestBody PreferenceRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(preferenceService.saveOrUpdatePreference(userEmail, request));
    }

    // PUT request ke liye (Updates)
    @PutMapping
    public ResponseEntity<String> updatePreferences(@Valid @RequestBody PreferenceRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(preferenceService.saveOrUpdatePreference(userEmail, request));
    }

    @GetMapping
    public ResponseEntity<PreferenceRequest> getPreferences(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(preferenceService.getUserPreference(userEmail));
    }
}