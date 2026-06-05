package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.service.DanceService;
import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ParticipantService participantService;
    private final DanceService danceService;

    @GetMapping("/")
    public String index(Model model) {
        var leaderboard = participantService.getLeaderboard();
        model.addAttribute("leaderboard", leaderboard.stream().limit(10).toList());
        model.addAttribute("totalParticipants", participantService.countTotal());
        model.addAttribute("totalDances", danceService.countTotal());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
