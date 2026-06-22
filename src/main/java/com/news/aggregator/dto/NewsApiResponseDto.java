package com.news.aggregator.dto;

import lombok.Data;
import java.util.List;

@Data
public class NewsApiResponseDto {
    private String status;
    private int totalResults;
    private List<ExternalArticleDto> articles;
}