package com.news.aggregator.service;

import com.news.aggregator.dto.PreferenceRequest;
import com.news.aggregator.model.User;
import com.news.aggregator.model.UserPreference;
import com.news.aggregator.repository.UserPreferenceRepository;
import com.news.aggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public String saveOrUpdatePreference(String email, PreferenceRequest request) {
        // 1. Token se mile email ke bihaaf par real user fetch karo
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. First Principle: Check Karo profile pehle se Bani hai ya nahi
        UserPreference preference = user.getPreference();

        if (preference == null) {
            // New Profile Creation
            preference = UserPreference.builder()
                    .categories(request.getCategories())
                    .sources(request.getSources())
                    .user(user)
                    .build();
            user.setPreference(preference); // Bi-directional link maintain kar rahe hain
        } else {
            // Existing Profile Update
            preference.setCategories(request.getCategories());
            preference.setSources(request.getSources());
        }

        preferenceRepository.save(preference);
        return "Preferences saved successfully!";
    }

    @Transactional(readOnly = true)
    public PreferenceRequest getUserPreference(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserPreference preference = user.getPreference();
        if (preference == null) {
            throw new RuntimeException("No preferences found for this user. Please set them first.");
        }

        PreferenceRequest response = new PreferenceRequest();
        response.setCategories(preference.getCategories());
        response.setSources(preference.getSources());
        return response;
    }
}