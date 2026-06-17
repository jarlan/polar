package com.polarsirkelrock.dancechallenge.service;

import com.polarsirkelrock.dancechallenge.dto.LeaderboardEntryDto;
import com.polarsirkelrock.dancechallenge.entity.DrawResult;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.DrawResultRepository;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrizeDrawService {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final DrawResultRepository drawResultRepository;

    @Transactional
    public DrawResult draw(int threshold) {
        List<LeaderboardEntryDto> leaderboard = participantService.getLeaderboard();
        List<LeaderboardEntryDto> eligible = leaderboard.stream()
                .filter(e -> e.getUniquePartners() >= threshold)
                .toList();

        if (eligible.isEmpty()) {
            throw new IllegalStateException("No eligible participants with >= " + threshold + " partners");
        }

        Random random = new Random();
        LeaderboardEntryDto winner = eligible.get(random.nextInt(eligible.size()));
        Participant winnerParticipant = participantRepository.findById(winner.getParticipantId())
                .orElseThrow();

        DrawResult result = DrawResult.builder()
                .winner(winnerParticipant)
                .threshold(threshold)
                .build();
        drawResultRepository.save(result);

        log.info("Prize draw: winner={} (threshold={})", winnerParticipant.getName(), threshold);
        return result;
    }

    public List<DrawResult> getHistory() {
        return drawResultRepository.findAllByOrderByDrawnAtDesc();
    }
}
