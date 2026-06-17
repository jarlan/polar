package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.dto.RegisterFormDto;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.service.DanceService;
import com.polarsirkelrock.dancechallenge.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final DanceService danceService;
    private final ParticipantService participantService;

    @GetMapping("/register")
    public String register(@RequestParam(required = false) Long partner, Model model) {
        if (partner != null) {
            participantService.findById(partner).ifPresent(p ->
                model.addAttribute("partnerName", p.getName())
            );
        }
        model.addAttribute("partnerId", partner);
        RegisterFormDto form = new RegisterFormDto();
        form.setPartnerId(partner);
        model.addAttribute("form", form);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute RegisterFormDto form, Model model) {
        try {
            boolean isNew = danceService.registerDance(form.getMyId(), form.getPartnerId());
            model.addAttribute("isNew", isNew);
            String partnerName = participantService.findById(form.getPartnerId())
                    .map(Participant::getName).orElse("Unknown");
            model.addAttribute("partnerName", partnerName);
            model.addAttribute("myId", form.getMyId());
            return "success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("partnerId", form.getPartnerId());
            model.addAttribute("form", form);
            return "register";
        }
    }
}
