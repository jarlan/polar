package com.polarsirkelrock.dancechallenge.repository;

import com.polarsirkelrock.dancechallenge.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByEmail(String email);

    @Query("""
        SELECT p FROM Participant p
        WHERE p.id IN (
            SELECT d.participantB.id FROM Dance d WHERE d.participantA.id = :id
            UNION ALL
            SELECT d.participantA.id FROM Dance d WHERE d.participantB.id = :id
        )
        """)
    List<Participant> findPartners(@Param("id") Long participantId);

    @Query("""
        SELECT COUNT(DISTINCT CASE WHEN d.participantA.id = :id THEN d.participantB.id ELSE d.participantA.id END)
        FROM Dance d
        WHERE d.participantA.id = :id OR d.participantB.id = :id
        """)
    long countUniquePartners(@Param("id") Long participantId);
}
