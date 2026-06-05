package com.polarsirkelrock.dancechallenge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dances",
    uniqueConstraints = @UniqueConstraint(columnNames = {"participant_a_id", "participant_b_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_a_id", nullable = false)
    private Participant participantA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_b_id", nullable = false)
    private Participant participantB;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
