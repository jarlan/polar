package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.dto.StatisticsDto;
import com.polarsirkelrock.dancechallenge.service.DanceService;
import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsApiController {

    private final ParticipantService participantService;
    private final DanceService danceService;

    @GetMapping
    public StatisticsDto getStatistics() {
        var leaderboard = participantService.getLeaderboard();
        String topDancer = leaderboard.isEmpty() ? "None" : leaderboard.getFirst().getName();
        long topPartners = leaderboard.isEmpty() ? 0 : leaderboard.getFirst().getUniquePartners();
        return new StatisticsDto(
            participantService.countTotal(),
            danceService.countTotal(),
            topDancer,
            topPartners
        );
    }
}
