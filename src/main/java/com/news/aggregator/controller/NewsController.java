package com.news.aggregator.controller;

import com.news.aggregator.dto.NewsApiResponseDto;
import com.news.aggregator.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/feed")
    public ResponseEntity<NewsApiResponseDto> getMyFeed(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(newsService.getPersonalizedFeed(userEmail));
    }
}