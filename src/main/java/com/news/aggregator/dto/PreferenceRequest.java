package com.news.aggregator.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Set;

@Data
public class PreferenceRequest {
    @NotEmpty(message = "Preferred categories cannot be empty")
    private Set<String> categories;

    @NotEmpty(message = "Preferred sources cannot be empty")
    private Set<String> sources;
}