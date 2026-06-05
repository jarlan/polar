package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/participant/{id}")
    public String participant(@PathVariable Long id, Model model) {
        var participant = participantService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + id));
        model.addAttribute("participant", participant);
        model.addAttribute("partners", participantService.findPartners(id));
        model.addAttribute("uniqueCount", participantService.countUniquePartners(id));
        return "participant";
    }
}
