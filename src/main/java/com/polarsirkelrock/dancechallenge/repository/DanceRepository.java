package com.polarsirkelrock.dancechallenge.repository;

import com.polarsirkelrock.dancechallenge.entity.Dance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DanceRepository extends JpaRepository<Dance, Long> {

    @Query("""
        SELECT d FROM Dance d
        WHERE (d.participantA.id = :aId AND d.participantB.id = :bId)
           OR (d.participantA.id = :bId AND d.participantB.id = :aId)
        """)
    Optional<Dance> findByPair(@Param("aId") Long aId, @Param("bId") Long bId);

    @Query("""
        SELECT d FROM Dance d
        WHERE d.participantA.id = :id OR d.participantB.id = :id
        ORDER BY d.timestamp DESC
        """)
    List<Dance> findByParticipant(@Param("id") Long participantId);

    @Query("""
        SELECT p.id, p.name,
               COUNT(DISTINCT CASE WHEN d.participantA.id = p.id THEN d.participantB.id ELSE d.participantA.id END) AS cnt
        FROM Participant p
        LEFT JOIN Dance d ON d.participantA.id = p.id OR d.participantB.id = p.id
        GROUP BY p.id, p.name
        ORDER BY cnt DESC
        """)
    List<Object[]> leaderboardRaw();
}
