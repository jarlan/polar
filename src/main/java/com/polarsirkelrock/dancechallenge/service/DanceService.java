package com.polarsirkelrock.dancechallenge.service;

import com.polarsirkelrock.dancechallenge.entity.Dance;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.DanceRepository;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import com.polarsirkelrock.dancechallenge.websocket.LeaderboardWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DanceService {

    private final DanceRepository danceRepository;
    private final ParticipantRepository participantRepository;
    private final LeaderboardWebSocketService webSocketService;
    private final ParticipantService participantService;

    @Autowired
    public DanceService(DanceRepository danceRepository,
                        ParticipantRepository participantRepository,
                        LeaderboardWebSocketService webSocketService,
                        @Lazy ParticipantService participantService) {
        this.danceRepository = danceRepository;
        this.participantRepository = participantRepository;
        this.webSocketService = webSocketService;
        this.participantService = participantService;
    }

    @Transactional
    public boolean registerDance(Long idA, Long idB) {
        if (idA.equals(idB)) {
            throw new IllegalArgumentException("A participant cannot dance with themselves");
        }

        // Normalize order: always store smaller id as participantA
        Long minId = Math.min(idA, idB);
        Long maxId = Math.max(idA, idB);

        if (danceRepository.findByPair(minId, maxId).isPresent()) {
            log.info("Dance pair already exists: {} + {}", minId, maxId);
            return false;
        }

        Participant a = participantRepository.findById(minId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + minId));
        Participant b = participantRepository.findById(maxId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + maxId));

        Dance dance = Dance.builder()
                .participantA(a)
                .participantB(b)
                .build();
        danceRepository.save(dance);
        log.info("Dance registered: {} + {}", minId, maxId);

        webSocketService.broadcastLeaderboard(participantService.getLeaderboard());

        return true;
    }

    public long countTotal() {
        return danceRepository.count();
    }
}
