package com.news.aggregator.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ElementCollection se database mein ek alag child table banti hai multivalue attributes ke liye
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_preferred_categories", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "category")
    private Set<String> categories; // e.g., {"technology", "sports", "finance"}

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_preferred_sources", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "source")
    private Set<String> sources; // e.g., {"bbc-news", "techcrunch"}

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}