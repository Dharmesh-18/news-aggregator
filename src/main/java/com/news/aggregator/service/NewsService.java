package com.news.aggregator.service;

import com.news.aggregator.dto.NewsApiResponseDto;
import com.news.aggregator.dto.PreferenceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final RestClient restClient;
    private final PreferenceService preferenceService;

    @Value("${news.api.key}")
    private String apiKey;

    @Cacheable(value = "newsFeed", key = "#email")
    public NewsApiResponseDto getPersonalizedFeed(String email) {
        System.out.println("!!! Cache MISS !!! Fetching fresh TOP HEADLINES from external API for: " + email);

        PreferenceRequest preferences = preferenceService.getUserPreference(email);

        // Default category set karte hain agar user ne kuch select nahi kiya ho
        String primaryCategory = "general";

        // User ki select ki hui pehli valid category ko pick karte hain top-headlines ke liye
        if (preferences.getCategories() != null && !preferences.getCategories().isEmpty()) {
            primaryCategory = preferences.getCategories().iterator().next();
        }

        // First Principle: Using /top-headlines endpoint with strict query parameter compliance
        String finalCategory = primaryCategory;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/top-headlines")
                        .queryParam("category", finalCategory) // Strict single category filter
                        .queryParam("language", "en")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(NewsApiResponseDto.class);
    }
}