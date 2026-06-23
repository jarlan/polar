package com.polarsirkelrock.dancechallenge.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrizeDrawCandidateDto {
  Long participantId;
  String name;
  int uniquePartners;
}
