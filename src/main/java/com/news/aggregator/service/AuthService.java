package com.news.aggregator.service;

import com.news.aggregator.dto.LoginRequest;
import com.news.aggregator.dto.RegisterRequest;
import com.news.aggregator.model.User;
import com.news.aggregator.repository.UserRepository;
import com.news.aggregator.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        // First Principle: Idempotency check (Duplicate user validation)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!"); // Baad mein global exception handler se handle karenge
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hashing before saving
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    public Map<String, String> login(LoginRequest request) {
        // First Principle: Spring Security ka internal authentication engine trigger karna
        // Yeh internally CustomUserDetailsService call karega aur password matching check karega
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Agar authentication failed hoti hai toh upar wali line exception throw karegi.
        // Agar success hoti hai, toh code yahan aayega aur hum token generate karenge.
        String token = jwtUtil.generateToken(request.getEmail());

        return Map.of("token", token);
    }
}