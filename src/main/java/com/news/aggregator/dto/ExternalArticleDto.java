package com.news.aggregator.dto;

import lombok.Data;

@Data
public class ExternalArticleDto {
    private String title;
    private String description;
    private String url;
    private String publishedAt;
    private SourceInfo source;

    @Data
    public static class SourceInfo {
        private String id;
        private String name;
    }
}