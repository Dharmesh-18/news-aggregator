package com.news.aggregator.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    // First Principle: Thread-safe in-memory map to hold separate rate-limiting buckets for each user
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Rule: Maximum 10 tokens (requests), aur har 1 minute mein 10 tokens fir se refill honge
        Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Hum current authenticated user ka email nikalte hain identify karne ke liye
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String key = httpRequest.getRemoteAddr(); // Fallback identifier as IP Address
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            key = authentication.getName(); // Primary identifier as User Email
        }

        // Agar is user ka bucket pehle se map mein nahi hai, toh naya banao
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());

        // First Principle: Try to consume 1 token for the incoming request
        if (bucket.tryConsume(1)) {
            // Token mil gaya! Request ko aage badhne do
            chain.doFilter(request, response);
        } else {
            // Token khatam! Send 429 Too Many Requests error cleanly
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Try again after a minute.\"}");
        }
    }
}