package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.dto.DanceRequestDto;
import com.polarsirkelrock.dancechallenge.dto.DanceResponseDto;
import com.polarsirkelrock.dancechallenge.service.DanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dances")
@RequiredArgsConstructor
public class DanceApiController {

    private final DanceService danceService;

    @PostMapping
    public ResponseEntity<DanceResponseDto> registerDance(@Valid @RequestBody DanceRequestDto request) {
        boolean isNew = danceService.registerDance(request.getParticipantId(), request.getPartnerId());
        String message = isNew ? "Dance registered successfully" : "Dance already registered";
        return ResponseEntity.ok(new DanceResponseDto(true, message));
    }
}
