package com.news.aggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        // First Principle: Setting low-level timeouts to prevent resource starvation
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds connection wait time
        factory.setReadTimeout(5000);    // 5 seconds data wait time

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://newsapi.org/v2") // NewsAPI base URL
                .build();
    }
}