package com.polarsirkelrock.dancechallenge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "draw_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DrawResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Participant winner;

    @Column(nullable = false)
    private int threshold;

    @Column(name = "drawn_at", nullable = false)
    private LocalDateTime drawnAt;

    @PrePersist
    public void prePersist() {
        if (drawnAt == null) drawnAt = LocalDateTime.now();
    }
}
