package com.polarsirkelrock.dancechallenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DanceRequestDto {
    @NotNull
    private Long participantId;
    @NotNull
    private Long partnerId;
}
