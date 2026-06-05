package com.polarsirkelrock.dancechallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class StatisticsDto {
    private long totalParticipants;
    private long totalDances;
    private String topDancer;
    private long topDancerPartners;
}
