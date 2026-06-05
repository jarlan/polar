package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.dto.LeaderboardEntryDto;
import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardApiController {

    private final ParticipantService participantService;

    @GetMapping
    public List<LeaderboardEntryDto> getLeaderboard() {
        return participantService.getLeaderboard();
    }
}
