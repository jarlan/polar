package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {

    private final ParticipantService participantService;

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("leaderboard", participantService.getLeaderboard());
        return "leaderboard";
    }
}
