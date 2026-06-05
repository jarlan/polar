package com.polarsirkelrock.dancechallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class LeaderboardEntryDto {
    private int rank;
    private Long participantId;
    private String name;
    private long uniquePartners;
}
