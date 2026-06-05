package com.polarsirkelrock.dancechallenge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participant {

    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
